package com.example.lgg.tt;

public abstract class NamedRunnable implements Runnable{
    protected final String name;

    public NamedRunnable(String format,Object... args){
        name = Util.format(format,args);
    }
    @Override
    public final void run(){
        String old = Thread.currentThread().getName();
        Thread.currentThread().setName(name);
        try {
            execute();
        }finally {
            Thread.currentThread().setName(old);
        }
    }

    protected abstract void execute();
}
