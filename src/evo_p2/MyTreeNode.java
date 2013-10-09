/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evo_p2;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fan0114
 */
class MyTreeNode {

    int type;   // 0=fixed val 1=variable x 2=variable y 3=+ 4=- 5=* 6=/
    double value;
    MyTreeNode leftchild;
    MyTreeNode rightchild;
    double cost;

    public MyTreeNode(double value) {
        this.type = 0;
        this.value = value;
    }

    public MyTreeNode(int type) {
        this.type = type;
        this.value = 0;
    }

    public MyTreeNode(MyTreeNode n) {
        this.type = n.type;
        this.value = n.value;
    }

    public boolean isLeaf() {
        if (type <= 2) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (isLeaf()) {
            String symbol = "";
            if (type == 0) {
                symbol = "" + value;
            } else if (type == 1) {
                symbol = "x";
            } else if (type == 2) {
                symbol = "y";
            } else {
                try {
                    throw new Exception("Invalid type " + type);
                } catch (Exception ex) {
                    Logger.getLogger(MyTreeNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return symbol;
        } else {
            String symbol = "";
            if (type == 3) {
                symbol = "+";
            } else if (type == 4) {
                symbol = "-";
            } else if (type == 5) {
                symbol = "*";
            } else if (type == 6) {
                symbol = "/";
            } else {
                try {
                    throw new Exception("Invalid type " + type);
                } catch (Exception ex) {
                    Logger.getLogger(MyTreeNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return "(" + leftchild.toString() + " " + symbol + " " + rightchild.toString() + ")";
        }
    }
}
