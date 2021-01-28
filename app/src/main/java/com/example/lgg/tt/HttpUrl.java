package com.example.lgg.tt;

import java.util.List;

import androidx.annotation.Nullable;

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

    }


}
