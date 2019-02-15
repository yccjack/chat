package com.ycc.chat.abst;


public class ReorderExample {
    int a = 0;
     boolean flag = false;

    public void writer() {
        a = 1;                   //1
        flag = true;             //2
    }

    public void reader() {
        System.out.println("");
        if (flag) {                //3
            int i = a * a;        //4
            System.out.println(i);
        }
    }

    public static void main(String[] args) throws Exception {
        ReorderExample example = new ReorderExample();
        Thread t1 = new Thread(() -> example.writer());
        Thread t2 = new Thread(() -> example.reader());
        t2.start();
        t1.start();
    }
}