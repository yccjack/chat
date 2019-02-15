package com.ycc.chat.abst;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadQuestion {
    private volatile AtomicInteger index = new AtomicInteger(1);

    public static void main(String[] args) {
        /**
         * 这里n代表线程数,可以修改或者录入
         */
        int n = 4;
        ThreadQuestion threadQuestion = new ThreadQuestion();
        threadQuestion.initThread(n);
    }

    /**
     * 初始化线程
     * @param n 线程数
     */
    private void initThread(int n) {
        Print print = new Print(n);
        for (int i = 1; i <= n; i++) {
            new Thread(new MyRunnable(i == n ? 0 : i, print)).start();
        }
    }

    /**
     * 最终执行的类
     */
    class Print {
        int bornNum;
        Print(int bornNum) {
            this.bornNum = bornNum;
        }
        synchronized void print(int remainder) {
            /**
             * 结束最后一次执行
             */
            if(index.get()>100){
                return;
            }
            /**
             * 这里在notifyAll的时候判断是否匹配当前循环数,匹配打印,不匹配wait.
             */
            if (index.get() % bornNum == remainder) {
                System.out.println(Thread.currentThread().getName() + ":" + (index.getAndIncrement()-1));
                notifyAll();
            }else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class MyRunnable implements Runnable {
        private Print print;
        private int i;
        MyRunnable(int i, Print print) {
            this.i=i;
            this.print = print;
        }
        @Override
        public void run() {
            while (index.get() <=100) {
                print.print(i);
            }
        }
    }
}
