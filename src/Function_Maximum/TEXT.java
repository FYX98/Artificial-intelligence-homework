package Function_Maximum;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TEXT {
    static int geneLength = 22;//基因长度
    static int individualSize = 16;//个体大小
    static int maxEvolve = 50;//最大进化
    static double crossoverProbability = 0.7;//交叉概率
    static double mutationProbability = 0.2;//变异概率
    static double sumFitness = 0.0;//适合度

    //    Cloneable接口是一个标记接口,也就是没有任何内容
    //    clone方法是在Object种定义的,是protected,只有实现了这个接口，才可以在该类的实例上调用clone方法,否则会抛出CloneNotSupportException。
    //    Object中默认的实现是一个浅拷贝,也就是表面拷贝,如果需要实现深层次拷贝的话,必须对类中可变域生成新的实例。
    static class individual implements Cloneable {
        double fitness;
        double Pl;
        double Pr;
        int[] gene;//定义基因数组


        public individual(double fitness, double pl, double pr, int[] gene) {
            super();
            this.fitness = fitness;
            Pl = pl;
            Pr = pr;
            this.gene = gene;
        }


        @Override
        public String toString() {
            return "individual [fitness=" + fitness + ", Pl=" + Pl + ", Pr=" + Pr + ", gene=" + Arrays.toString(gene)
                    + "]";
        }


        public Object clone() {
            individual idd = null;
            try {
                idd = (individual) super.clone();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            assert idd != null;
            idd.gene = this.gene.clone();
            return idd;
        }
    }

    static List<individual> initPop() {
        List<individual> population = new ArrayList<>();
        int ca = 0;
        while (ca < individualSize) {
            int[] gene = new int[geneLength];
            for (int i = 0; i < geneLength; i++) {
                gene[i] = (int) (Math.random() * 100) % 2;
            }
            population.add(new individual(0.0, 0.0, 0.0, gene));
            ca++;
        }
        return population;
    }

    static void updateFitness(List<individual> population) {
        int constant = 4194103;
        for (int i = 0; i < individualSize; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < geneLength; j++) {
                sb.append(population.get(i).gene[j]);
            }
            int H = Integer.valueOf(sb.toString(), 2);
            double x = -1.0 + 3.0 * (H * 1.0 / constant * 1.0);
            population.get(i).fitness = x * Math.sin(10 * Math.PI * x) + 2.0;
            sumFitness += population.get(i).fitness;
        }
    }


    static individual bestIndividual(List<individual> population) {
        individual best = (individual) population.get(0).clone();
        for (int i = 1; i < individualSize; i++) {
            if (population.get(i).fitness > best.fitness) {
                best = (individual) population.get(i).clone();
            }
        }
        return best;
    }

    static List<individual> selectPop(List<individual> population) {
        int ca = 0;
        List<individual> newPopulation = new ArrayList<>();
        double start = 0.0;
        for (int i = 0; i < individualSize; i++) {
            double p = population.get(i).fitness / sumFitness;
            population.get(i).Pl = start;
            population.get(i).Pr = p + start;
            start = population.get(i).Pr;

        }
        while (ca < individualSize) {
            boolean flag = false;
            double P = Math.random();
            for (int j = 0; j < individualSize; j++) {
                if (P > population.get(j).Pl && P < population.get(j).Pr) {
                    newPopulation.add(population.get(j));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                newPopulation.add(bestIndividual(population));
            }
            ca++;
        }
        return newPopulation;
    }

    static void crossover(List<individual> population) {
        boolean flag = false;
        int father = 0;
        int mother;

        for (int i = 0; i < individualSize; i++) {
            double P = Math.random();
            if (P < crossoverProbability) {
                if (flag) {
                    mother = i;
                    int l = (int) (Math.random() * geneLength);
                    int r = (int) (Math.random() * geneLength);
                    if (l > r) {
                        int t = l;
                        l = r;
                        r = t;
                    }
                    int[] t = new int[geneLength];
                    for (int f = l; f < r; f++) {
                        t[f] = population.get(father).gene[f];
                        population.get(father).gene[f] = population.get(mother).gene[f];
                        population.get(mother).gene[f] = t[f];
                    }
                    flag = false;
                } else {
                    father = i;
                    flag = true;
                }
            }
        }
    }

    static void mutation(List<individual> population) {
        for (int i = 0; i < individualSize; i++) {
            double p = Math.random();
            if (p < mutationProbability) {
                int cnt = (int) (Math.random() * geneLength);
                while (cnt > 0) {
                    int point = (int) (Math.random() * (geneLength - 1));
                    population.get(i).gene[point] = 1 - population.get(i).gene[point];
                    cnt -= 1;
                }
            }
        }


        for (int i = 0; i < geneLength; i++) {
            int count = 0;
            for (int j = 0; j < individualSize; j++) {
                count += population.get(j).gene[i];
            }


            if (count < 3 || count > (individualSize - 3)) {
                int cn = (int) (Math.random() * individualSize);
                population.get(cn).gene[i] = 1 - population.get(cn).gene[i];
                int cn1 = (int) (Math.random() * individualSize);
                while (cn1 == cn) {
                    cn1 = (int) (Math.random() * individualSize);
                }
                population.get(cn1).gene[i] = 1 - population.get(cn1).gene[i];
            }
        }


    }

    public static void main(String[] args) {
        int ca = 0;
        List<individual> population = initPop();
        updateFitness(population);
        while (ca < maxEvolve) {
            List<individual> newP = selectPop(population);
            crossover(newP);
            mutation(newP);
            population = newP;
            updateFitness(population);
            ca++;
            System.out.println();
            for (int i = 0; i < individualSize; i++) {
                System.out.print("基因：" + Arrays.toString(population.get(i).gene) + " ");
                System.out.println("函数值：" + population.get(i).fitness);
            }
        }
        System.out.println("函数最优解为：");
        individual P = bestIndividual(population);
        System.out.print("基因：" + Arrays.toString(P.gene) + " ");
        System.out.println("函数值：" + P.fitness);
    }
}

