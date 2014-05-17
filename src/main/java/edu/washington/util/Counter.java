/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.util;

/**
 *
 * @author nickchen
 */
public class Counter {

    private int count;

    public Counter() {
        this(0);
    }

    public Counter(int count) {
        this.count = count;
    }

    public int next() {
        return ++count;
    }
}
