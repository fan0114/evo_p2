/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evo_p2;

/**
 *
 * @author fan0114
 */
class MyEquation {

    MyTreeNode root;
    double cost;
    double offset;

    public MyEquation(int type) throws Exception {
        if (type == 0) {
            throw new Exception();
        }
        root = new MyTreeNode(type);
    }

    public MyEquation(MyTreeNode root) throws Exception {
        this.root = root;
        double[] evaluation = Evo_p2.evaluate(this);
        this.cost = evaluation[0];
        this.offset = evaluation[1];
    }

    public double error(MyPoint mp, double offset) throws Exception {
        double ret = Math.abs(eval(root, mp) - Math.pow(mp.y - offset, 2));
//        System.out.println(this);
//        System.out.println("x=" + mp.x + " y=" + mp.y + " error=" + ret);
        return ret;
    }

    public double eval(MyTreeNode current, MyPoint mp) throws Exception {
        if (current.type == 0) {
            //fixed
            return current.value;
        } else if (current.type == 1) {
            //x
            return mp.x;
        } else if (current.type == 2) {
            //y
            return mp.y;
        } else if (current.type == 3) {
            //+
            return eval(current.leftchild, mp) + eval(current.rightchild, mp);
        } else if (current.type == 4) {
            //-
            return eval(current.leftchild, mp) - eval(current.rightchild, mp);
        } else if (current.type == 5) {
            //*
            return eval(current.leftchild, mp) * eval(current.rightchild, mp);
        } else if (current.type == 6) {
            ///
            return eval(current.leftchild, mp) / eval(current.rightchild, mp);
        } else {
            throw new Exception("Incorrect type " + current.type);
        }

    }

    @Override
    public String toString() {
        return "(y-" + offset + ")^2 = " + root.toString();
    }
}
