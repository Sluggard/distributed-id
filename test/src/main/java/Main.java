/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * Main
 *
 * @author Jun.An3
 * @date 2022/08/24
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(3 | 3);
        //00000001
        //00000001
        //00000001
        System.out.println(1 | 4);
        //00000001
        //00000100
        //00000101
        System.out.println(3 | 6);
        //00000011
        //00000110
        //00000111
        System.out.println((1 & 0) == 0);
        //00000001
        //00000000
        //00000000
    }

}
