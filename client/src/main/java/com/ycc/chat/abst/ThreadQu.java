package com.ycc.chat.abst;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public class ThreadQu {
    private volatile AtomicInteger index = new AtomicInteger(1);

    public static void main(String[] args) {
        ThreadQu t = new ThreadQu();
        t.test();
    }

    private void test(){
        Print m = new Print();
        new Thread1(m);
        new Thread2(m);
        new Thread3(m);
    }

    class Print{
        public synchronized void print(int remainder){
            if(index.get()%3==remainder){
                System.out.println(Thread.currentThread().getName()+":"+(index.get()-1));
                index.incrementAndGet();
                notifyAll();
            }
            if(index.get()<100){	//如果不加这一句，最后会有一个线程挂起
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread1 implements Runnable {
        Print m;
        public Thread1(Print m1) {
            this.m = m1;
            new Thread(this, "Thread0").start();
        }

        public void run() {
            while(true){
                if(index.get()>100){
                    break;
                }
                m.print(1);
            }
        }
    }

    class Thread2 implements Runnable {
        Print m;
        public Thread2(Print m2) {
            this.m = m2;
            new Thread(this, "Thread1").start();
        }
        public void run() {
            while(true){
                if(index.get()>100){
                    break;
                }
                m.print(2);
            }
        }
    }
    class Thread3 implements Runnable {
        Print m;
        public Thread3(Print m3) {
            this.m = m3;
            new Thread(this, "Thread2").start();
        }
        public void run() {
            while(true){
                if(index.get()>100){
                    break;
                }
                m.print(0);
            }
        }
    }

}
