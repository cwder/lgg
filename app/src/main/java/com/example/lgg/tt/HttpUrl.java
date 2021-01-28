package com.example.lgg.tt;

import android.text.TextUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import okhttp3.internal.Util;
import okio.Buffer;

public final class HttpUrl {
    private static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
    static final String QUERY_ENCODE_SET = " \"'<>#";
    static final String QUERY_COMPONENT_ENCODE_SET = " \"'<>#&=";
    static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
    static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    static final String FRAGMENT_ENCODE_SET = "";
    static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";


    final String scheme;

    private final String username;

    /** Decoded password. */
    private final String password;

    /** Canonical hostname. */
    final String host;

    /** Either 80, 443 or a user-specified port. In range [1..65535]. */
    final int port;

    private final List<String> pathSegments;

    private final @Nullable List<String> queryNamesAndValues;

    private final @Nullable String fragment;

    private final String url;

    public final class Builder{
        @Nullable String scheme;
        String encodedUsername = "";
        String encodedPassword = "";

        @Nullable String host;

        int port = -1;

        final List<String> encodedPathSegments = new ArrayList<>();

        @Nullable List<String> encodedQueryNamesAndValues;

        @Nullable String encodedFragment;

        Builder(){
            encodedPathSegments.add("");
        }

        public Builder scheme(String scheme){
            if (scheme == null){
                throw new NullPointerException("scheme == null");
            }else if(scheme.equalsIgnoreCase("http")){
                this.scheme = "http";
            }else if(scheme.equalsIgnoreCase("https")){
                this.scheme = "https";
            }else{
                throw new IllegalArgumentException("unexpected scheme: " + scheme);
            }

            return this;

        }

        public Builder username(String username){
            if (username == null){
                throw new NullPointerException("username == null");
            }

            this.encodedUsername = canonicalize(username, USERNAME_ENCODE_SET, false, false, false, true);
            return this;
        }

        public Builder encodedUsername(){
            if (encodedUsername == null){
                throw new NullPointerException("encodedUsername == null");
            }

            this.encodedUsername = canonicalize(
                    encodedUsername, USERNAME_ENCODE_SET, true, false, false, true);

            return this;
        }

        public Builder password(String password) {
            if (password == null) throw new NullPointerException("password == null");
            this.encodedPassword = canonicalize(password, PASSWORD_ENCODE_SET, false, false, false, true);
            return this;
        }

        public Builder encodedPassword(String encodedPassword) {
            if (encodedPassword == null) throw new NullPointerException("encodedPassword == null");
            this.encodedPassword = canonicalize(
                    encodedPassword, PASSWORD_ENCODE_SET, true, false, false, true);
            return this;
        }



    }



    static String canonicalize(String input, int pos, int limit, String encodeSet,
                               boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly,
                               Charset charset) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (codePoint < 0x20
                    || codePoint == 0x7f
                    || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))
                    || codePoint == '+' && plusIsSpace) {
                // Slow path: the character at i requires encoding!
                Buffer out = new Buffer();
                out.writeUtf8(input, pos, i);
                canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace,
                        asciiOnly, charset);
                return out.readUtf8();
            }
        }

        // Fast path: no characters in [pos..limit) required encoding.
        return input.substring(pos, limit);
    }

    static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet,
                             boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly,
                             Charset charset) {
        Buffer encodedCharBuffer = null; // Lazily allocated.
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (alreadyEncoded
                    && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
                // Skip this character.
            } else if (codePoint == '+' && plusIsSpace) {
                // Encode '+' as '%2B' since we permit ' ' to be encoded as either '+' or '%20'.
                out.writeUtf8(alreadyEncoded ? "+" : "%2B");
            } else if (codePoint < 0x20
                    || codePoint == 0x7f
                    || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))) {
                // Percent encode this character.
                if (encodedCharBuffer == null) {
                    encodedCharBuffer = new Buffer();
                }

                if (charset == null || charset.equals(Util.UTF_8)) {
                    encodedCharBuffer.writeUtf8CodePoint(codePoint);
                } else {
                    encodedCharBuffer.writeString(input, i, i + Character.charCount(codePoint), charset);
                }

                while (!encodedCharBuffer.exhausted()) {
                    int b = encodedCharBuffer.readByte() & 0xff;
                    out.writeByte('%');
                    out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
                    out.writeByte(HEX_DIGITS[b & 0xf]);
                }
            } else {
                // This character doesn't need encoding. Just copy it over.
                out.writeUtf8CodePoint(codePoint);
            }
        }
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict,
                               boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        return canonicalize(
                input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly,
                charset);
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict,
                               boolean plusIsSpace, boolean asciiOnly) {
        return canonicalize(
                input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, null);
    }

}
