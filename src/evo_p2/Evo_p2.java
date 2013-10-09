/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evo_p2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 *
 * @author fan0114
 */
public class Evo_p2 {

    public static double pSafe = 0.75;
    public static int evals;
    public static int population = 200;
    public static double offset = 3;
    static String filename = "SR_ellipse_noise.txt";
    static ArrayList<MyPoint> points;
    ArrayList<MyEquation> equations;
    static int initDepth = 6;
    static Comparator<MyEquation> cList = new Comparator<MyEquation>() {

        @Override
        public int compare(MyEquation l1, MyEquation l2) {
            double tmp = l1.cost - l2.cost;
            if (tmp < 0) {
                return -1;
            } else if (tmp == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    public Evo_p2() {
        evals = 0;
    }

    //1 is x, 2 is y
    static boolean hasType(MyTreeNode n, int type) {
        if (n.isLeaf()) {
            if (n.type == type) {
                return true;
            } else {
                return false;
            }
        }
        return (hasType(n.leftchild, type) || hasType(n.rightchild, type));
    }

    static boolean isValidRoot(MyTreeNode root) {
        //not a leaf
        if (root.isLeaf()) {
            return false;
        }
        //not 0/a or 0*a or a/0 or a*0
        if (root.type == 5) {
            //*
            if (root.leftchild.type == 0 && root.leftchild.value == 0.0) {
                return false;
            }
            if (root.rightchild.type == 0 && root.rightchild.value == 0.0) {
                return false;
            }
        }
        if (root.type == 6) {
            if (root.leftchild.type == 0 && root.leftchild.value == 0.0) {
                return false;
            }
            if (root.rightchild.type == 0 && root.rightchild.value == 0.0) {
                return false;
            }
        }
        //contains only x no y
        if (hasType(root, 1) && !hasType(root, 2)) {
            return true;
        }
        return false;
    }

    static boolean isEqualNode(MyTreeNode n1, MyTreeNode n2) {
        if (n1.type == 0 && n2.type == 0) {
            //both fixed
            if (n1.value == n2.value) {
                return true;
            } else {
                return false;
            }
        } else if (n1.type == n2.type) {
            if (n1.type == 1 || n1.type == 2) {
                //x or y
                return true;
            } else {
                // + - * /
                if ((isEqualNode(n1.leftchild, n2.leftchild) && isEqualNode(n1.rightchild, n2.rightchild))
                        || (isEqualNode(n1.leftchild, n2.rightchild) && isEqualNode(n1.rightchild, n2.leftchild))) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    //replace exist node
    static void nodecpy(MyTreeNode nodeto, MyTreeNode nodefrom) {
        nodeto.leftchild = nodefrom.leftchild;
        nodeto.rightchild = nodefrom.rightchild;
        nodeto.type = nodefrom.type;
        nodeto.value = nodefrom.value;
    }

    static void prune(MyTreeNode n) throws Exception {
        //System.out.println("\tprune " + n);
        if (n.isLeaf()) {
            return;
        }
        prune(n.leftchild);
        prune(n.rightchild);

        if (isEqualNode(n.leftchild, n.rightchild)
                && n.type == 4) {
            //System.out.println("\t\t" + n.leftchild + "=" + n.rightchild + " is " + isEqualNode(n.leftchild, n.rightchild));
            //a-a
            n.type = 0;
            n.value = 1e-10;
            return;
        } else if (isEqualNode(n.leftchild, n.rightchild)
                && n.type == 6) {
            //System.out.println("\t\t" + n.leftchild + "=" + n.rightchild + " is " + isEqualNode(n.leftchild, n.rightchild));
            //a/a
            n.type = 0;
            n.value = 1;
            return;
        } else if ((n.rightchild.type == n.leftchild.type)
                && n.leftchild.type == 0) {
            //fixed
            if (n.type == 3) {
                n.type = 0;
                n.value = n.leftchild.value + n.rightchild.value;
            } else if (n.type == 4) {
                n.type = 0;
                n.value = n.leftchild.value - n.rightchild.value;
            } else if (n.type == 5) {
                n.type = 0;
                n.value = n.leftchild.value * n.rightchild.value;
            } else if (n.type == 6) {
                n.type = 0;
                n.value = n.leftchild.value / n.rightchild.value;
            } else {
                throw new Exception("Invalid type " + n.type);
            }
            return;
        } else if (n.type == 3 || n.type == 4) {
            //a+0 or 0+a or a-0 or 0-a
            if (n.leftchild.type == 0 && n.leftchild.value == 0.0) {
                nodecpy(n, n.rightchild);
            } else if (n.rightchild.type == 0 && n.rightchild.value == 0.0) {
                nodecpy(n, n.leftchild);
            }
        } else if (n.type == 5) {
            //a*0 or 0*a
            if ((n.leftchild.type == 0 && n.leftchild.value == 0.0) || (n.rightchild.type == 0 && n.rightchild.value == 0.0)) {
                n.type = 0;
                n.value = 0.0;
            }
        } else if (n.type == 6) {
            if (n.rightchild.type == 0 && n.rightchild.value == 0.0) {
                //a/0
                throw new Exception("cannot divided by zero n type=" + n.rightchild.type + " value=" + n.rightchild.value);
            } else if (n.leftchild.type == 0 && n.leftchild.value == 0.0) {
                //0/a
                n.type = 0;
                n.value = 0.0;
            }
        }
    }

    public static double[] evaluate(MyEquation eq) throws Exception {
        evals++;
        double[] ret = new double[2];
        double minsum = 1e100;
        double minoffset = 0;
        for (double ofst = 0 - offset; ofst < offset; ofst += 0.05) {
            double sum = 0;
            for (MyPoint mp : points) {
                sum += eq.error(mp, ofst);
            }
            if (sum < minsum) {
                minsum = sum;
                minoffset = ofst;
            }
        }
        //return sum;
        ret[0] = minsum;
        ret[1] = minoffset;
        return ret;
    }

    public MyEquation newEquation() throws Exception {
        MyTreeNode root = buildRandomTree(initDepth);
        Evo_p2.prune(root);
        while (!isValidRoot(root)) {
            root = buildRandomTree(initDepth);
            Evo_p2.prune(root);
        }
        MyEquation r = new MyEquation(root);
        return r;
    }

    public MyTreeNode buildRandomTree(int depth) {
        Random generator = new Random();
        int type;
        MyTreeNode r;
        if (depth == 1) {
            type = generator.nextInt(2);
            if (type == 0) {
                //fixed
                r = new MyTreeNode((double) (generator.nextDouble() + 0.00001));
            } else {
                //x or y
                r = new MyTreeNode(type);
            }
        } else {
            type = generator.nextInt(4) + 3;
            r = new MyTreeNode(type);
            r.leftchild = buildRandomTree(depth - 1);
            r.rightchild = buildRandomTree(depth - 1);
            //System.out.println("right type:" + r.rightchild.type + " value:" + r.rightchild.value);
        }
        return r;
    }

    //copy
    static MyTreeNode treecpy(MyTreeNode copyfrom) {
        MyTreeNode r = new MyTreeNode(copyfrom);
        if (!copyfrom.isLeaf()) {
            r.leftchild = treecpy(copyfrom.leftchild);
            r.rightchild = treecpy(copyfrom.rightchild);
        }
        return r;
    }

    static void addLeafs(ArrayList<MyTreeNode> leafs, MyTreeNode n) {
        if (n.isLeaf()) {
            leafs.add(n);
        } else {
            addLeafs(leafs, n.leftchild);
            addLeafs(leafs, n.rightchild);
        }
    }

    static MyEquation offspring(MyEquation l1, MyEquation l2) throws Exception {
        MyTreeNode newroot;
        do {
            //crossover
            Random generator = new Random();

            newroot = treecpy(l1.root);

            //swap sub-tree
            //select substitute sub-tree
            MyTreeNode current = l2.root;
            MyTreeNode subtree;
            double isswap;
            do {
                isswap = generator.nextDouble();
                boolean goleft = generator.nextBoolean();
                if (goleft) {
                    current = current.leftchild;
                } else {
                    current = current.rightchild;
                }
            } while (!current.isLeaf() && isswap < 0.2);
            subtree = treecpy(current);

            current = newroot;
            isswap = generator.nextDouble();
            do {
                isswap = generator.nextDouble();
                boolean goleft = generator.nextBoolean();
                if (goleft) {
                    current = current.leftchild;
                } else {
                    current = current.rightchild;
                }
            } while (!current.isLeaf() && isswap < 0.2);

            boolean goleft = generator.nextBoolean();
            if (goleft) {
                current.leftchild = subtree;
            } else {
                current.rightchild = subtree;
            }

            System.gc();    //suggest java run garbage collector for each run of generating offspring
            //mutation: 
            ArrayList<MyTreeNode> leafs = new ArrayList<MyTreeNode>();

            addLeafs(leafs, newroot);

            int randomNode = generator.nextInt(leafs.size());
            int randomType = generator.nextInt(2);
            double randomValue = generator.nextDouble() - 0.5;
            leafs.get(randomNode).type = randomType;
            leafs.get(randomNode).value += randomValue;
            //System.out.print("\tprune newroot: "+newroot);
            Evo_p2.prune(newroot);
            //System.out.println(" is "+newroot);
        } while (!isValidRoot(newroot));

        MyEquation ret = new MyEquation(newroot);

        return ret;
    }

    public void run() throws Exception {
        double cost;
        cost = equations.get(0).cost;
        double newcost = cost;
        ArrayList<MyEquation> newlists = (ArrayList<MyEquation>) equations.clone();
//        while (newcost >= cost) {
        int oldevals = evals;
        while ((evals - oldevals < 10)) {
            //random pick
            double grandtotalcost = 0;
            for (int i = 0; i < equations.size(); i++) {
                grandtotalcost += 1 / equations.get(i).cost;
            }
            for (int i = (int) (equations.size() * pSafe); i < equations.size(); i++) {
                MyEquation p1 = equations.get(0);
                MyEquation p2 = equations.get(1);
                double p = Math.random();
                double cumulativeProbability = 0.0;
                for (MyEquation item : equations) {
                    cumulativeProbability += 1 / item.cost / grandtotalcost;
                    if (p <= cumulativeProbability) {
                        p1 = item;
                        break;
                    }
                }
                p = Math.random();
                cumulativeProbability = 0.0;
                for (MyEquation item : equations) {
                    cumulativeProbability += 1 / item.cost / grandtotalcost;
                    if (p <= cumulativeProbability) {
                        p2 = item;
                        break;
                    }
                }
                MyEquation newEquation = offspring(p1, p2);
                newlists.set(i, newEquation);
            }
            Collections.sort(newlists, cList);
            equations = newlists;
            newcost = equations.get(0).cost;
        }
//        System.out.println(equations.get(0));
        System.out.print(newcost);
        System.out.println("|" + evals);
        if (newcost == 0.0) {
            System.exit(0);
        }
    }

    public double[][] run2() throws Exception {
        double[][] result = new double[points.size()][4];
        double cost;
        cost = equations.get(0).cost;
        double newcost = cost;
        ArrayList<MyEquation> newlists = (ArrayList<MyEquation>) equations.clone();
//        while (newcost >= cost) {
        int oldevals = evals;
        while ( newcost >= cost) {
            //random pick
            double grandtotalcost = 0;
            for (int i = 0; i < equations.size(); i++) {
                grandtotalcost += 1 / equations.get(i).cost;
            }
            for (int i = (int) (equations.size() * pSafe); i < equations.size(); i++) {
                MyEquation p1 = equations.get(0);
                MyEquation p2 = equations.get(1);
                double p = Math.random();
                double cumulativeProbability = 0.0;
                for (MyEquation item : equations) {
                    cumulativeProbability += 1 / item.cost / grandtotalcost;
                    if (p <= cumulativeProbability) {
                        p1 = item;
                        break;
                    }
                }
                p = Math.random();
                cumulativeProbability = 0.0;
                for (MyEquation item : equations) {
                    cumulativeProbability += 1 / item.cost / grandtotalcost;
                    if (p <= cumulativeProbability) {
                        p2 = item;
                        break;
                    }
                }
                MyEquation newEquation = offspring(p1, p2);
                newlists.set(i, newEquation);
            }
            Collections.sort(newlists, cList);
            equations = newlists;
            newcost = equations.get(0).cost;
        }
        for (int i = 0; i < points.size(); i++) {
            result[i][0] = points.get(i).x;
            double[] tmp = solve(equations.get(0), points.get(i).x);
            result[i][1] = tmp[0];
            result[i][2] = tmp[1];
            result[i][3] = points.get(i).y;
            //System.out.println("(" + result[i][0] + ", " + result[i][1] + ", " + result[i][2] + ", " + result[i][3] + ")");
        }
        System.out.println(equations.get(0));
        System.out.print(newcost);
        System.out.println("|" + evals);
        return result;
    }

    public void init() throws FileNotFoundException, IOException, Exception {
        // TODO code application logic here
        //ArrayList<Node> list = new ArrayList<Node>();
        points = new ArrayList<MyPoint>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            String[] nums = line.split(" ");
            //Node node = new Node(Double.parseDouble(nums[0]), Double.parseDouble(nums[1]), count);
            double[] tmp = new double[2];
            int tmpaddr = 0;
            for (int i = 0; i < nums.length; i++) {
                if (!nums[i].equals("")) {
                    //System.out.println("nums: |" + nums[i] + "|");
                    tmp[tmpaddr] = Double.parseDouble(nums[i]);
                    tmpaddr++;
                }
            }
            if (tmpaddr != 2) {
                System.out.println("tmpaddr=" + tmpaddr);
                System.exit(0);
            }
            points.add(new MyPoint(tmp[0], tmp[1]));
            count++;
            //System.out.println("count: " + count);
        }
        //init trees
        equations = new ArrayList<MyEquation>();
        for (int i = 0; i < population; i++) {
            equations.add(newEquation());
        }
        Collections.sort(equations, cList);
        for (int i = 0; i < population; i++) {
            //System.out.println(equations.get(i));
            // System.out.println("Cost: " + equations.get(i).cost);
        }
    }

    public double[] solve(MyEquation eq, double x) throws Exception {
//        double min = 1e100;
//        double besty = -100;
//        for (double y = 0; y < 3; y += 0.0001) {
//            MyPoint mp = new MyPoint(x, y);
//            double gap = eq.error(mp);
//            if (gap < min) {
//                min = gap;
//                besty = y;
//            }
//        }
//        return besty;
        double[] ret = new double[2];
        MyPoint mp = new MyPoint(x, 0);
        double left2 = eq.eval(eq.root, mp);
        double left = Math.sqrt(left2);
        ret[0] = left + eq.offset;
        ret[1] = eq.offset - left;

        return ret;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        // TODO code application logic here
        Evo_p2 instance = new Evo_p2();
        instance.init();

        //run
        while (evals < 5000) {
            instance.run();
        }

    }
}
