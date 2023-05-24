import javax.naming.InsufficientResourcesException;
import java.io.*;
import java.util.*;

public class IterMethod {
    static final int a = 9;
    static final int b = 9;

    public static void main(String[] args) throws FileNotFoundException {
        //InputMatrixToFile(a, b); //записываем рандомную матрицу в файл
        int[][] array = OutputMatrixFromFile(a, b); //считываем матрицу из файла
        if(SaddlePoint(array)){
            System.exit(0);
        }
        //первый вариант
        System.out.println("\nGame ONE!\n");
        iterMethod(8, 0, array);
        //второй вариант
        System.out.println("\nGAME TWO!\n");
        iterMethod(8, 5, array);
    }

    //заносим матрицу в файл
    public static void InputMatrixToFile(int a, int b) throws FileNotFoundException {
        File file = new File("test");
        PrintWriter pw = new PrintWriter(file);
        int[][] array = new int[a][b];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                array[i][j] = (int) (Math.random() * 10);
                pw.print(array[i][j] + " ");
            }
            pw.println();
        }
        pw.close();
    }

    //чтение матрицы из файла
    public static int[][] OutputMatrixFromFile(int a, int b) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("test"));
        int[][] array = new int[a][b];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                array[i][j] = scanner.nextInt();
                scanner.hasNextInt();
            }
        }
        System.out.println("Iznachal'nii massive");
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
        return array;
    }

    //Определение есть ли у нас седловая точка
    public static boolean SaddlePoint(int[][] massive){
        int min = 0, max = 0;
        List<Integer> minArray = new ArrayList<>();
        List<Integer> maxArray = new ArrayList<>();
        boolean saddlePoint = false;
        //Находим минимальный элемент в строках
        //проходимся по массиву полностью
        for (int[] ints : massive) {
            min = ints[0];
            //мы проходимся именно по строке
            for (int anInt : ints) {
                if (min > anInt)
                    min = anInt;
            }
            minArray.add(min);
        }

        //Находим максимальный элемент в столбцах
        for(int j = 0; j < massive.length; j++){
            max = massive[0][j];
            for(int i = 0; i < massive[0].length; i++){
                if(massive[i][j] > max)
                    max = massive[i][j];
            }
            maxArray.add(max);
        }

        //Узнаём, есть ли максимальная точка
        if(Objects.equals(Collections.max(minArray), Collections.min(maxArray))){
            System.out.printf("Saddle point massive[%d][%d] = %d! End the algorithm!", max, min, massive[max][min]);
            saddlePoint = true;
        }
        if(!saddlePoint)
            System.out.println("Saddle point not found! We can calculate the algorithm further.");

        return saddlePoint;
    }

    public static void iterMethod(int iter, int firstStrategy, int[][] massive){
        List<Double> minMaxGamma = new ArrayList<>();
        List<Double> maxMinGamma = new ArrayList<>();
        Map<Integer, Integer> Sa = new HashMap<>();
        Map<Integer, Integer> Sb = new HashMap<>();
        int[][] mass1 = new int[2][b];
        int[][] mass2 = new int[2][b];
        double gamma2 = 0.0;
        double gamma1 = 0.0;
        int counter = 0;
        int indexOfMin = 0;
        int indexOfMax = firstStrategy;

        for(int k = 0; k < iter; k++) {
            if(counter == 0) {
                //берем первую стратегию для нашей игры для игрока B
                System.arraycopy(massive[indexOfMax], 0, mass1[0], 0, massive[indexOfMax].length);
            }
            else {
                //если стратегия не первая, тогда производим суммирование двух строк из матрицы
                for (int i = 0; i < massive[indexOfMax].length; i++) {
                    mass1[0][i] += massive[indexOfMax][i];
                }
            }
            //перезаписываем массив для дальнейшего суммирования
            System.arraycopy(mass1[0], 0, mass1[1], 0, mass1[0].length);
            //находим минимальное значение в Массиве B
            int min = mass1[0][0];
            indexOfMin = 0;
            for (int i = 0; i < mass1[0].length; i++) {
                if (mass1[0][i] < min) {
                    min = mass1[0][i];
                    indexOfMin = i;
                }
            }

            //записываем в динамический массив минимальные значения для будущего нахождения максимального среди них
            //и высчитываем Gamma2.
            gamma2 = (double) min/(counter+1);
            minMaxGamma.add(gamma2);

            //Находим Sb
            int indexOfMinSb = indexOfMin + 1;
            if(Sb.containsKey(indexOfMinSb)){
                Sb.replace(indexOfMinSb, Sb.get(indexOfMinSb) + 1);
            } else {
                Sb.put(indexOfMinSb, 1);
            }

            if(counter == 0) {
                //берем первую стратегию для нашей игры для игрока А
                for(int j = 0; j < massive.length; j++){
                    mass2[0][j] = massive[j][indexOfMin];
                }
            }
            else {
                //если стратегия не первая, тогда производим суммирование двух строк из матрицы
                for (int j = 0; j < massive[indexOfMin].length; j++) {
                    mass2[0][j] += massive[j][indexOfMin];
                }
            }
            //перезаписываем массив для дальнейшего суммирования
            System.arraycopy(mass2[0], 0, mass2[1], 0, mass2[0].length);

            //находим максимальное значение в массиве A
            int max = mass2[0][0];
            indexOfMax = 0;
            for (int i = 0; i < mass2[0].length; i++){
                if(mass2[0][i] > max) {
                    max = mass2[0][i];
                    indexOfMax = i;
                }
            }

            //записываем в динамический массив максимальное значения для будущего нахождения минмального среди них
            //и высчитываем Gamma1
            gamma1 = (double) max/(counter+1);
            maxMinGamma.add(gamma1);

            //Находим Sa
            int indexOfMaxSa = indexOfMax + 1;
            if(Sa.containsKey(indexOfMaxSa)){
                Sa.replace(indexOfMaxSa, Sa.get(indexOfMaxSa) + 1);
            } else {
                Sa.put(indexOfMaxSa, 1);
            }

            counter++;

            System.out.println("Iteration number: " + counter);
            System.out.println("Matrix B: ");
            for (int j = 0; j < mass1[0].length; j++)
                System.out.print(mass1[0][j] + " ");
            System.out.println();
            System.out.println("Matrix A: ");
            for (int j = 0; j < mass2[0].length; j++)
                System.out.print(mass2[0][j] + " ");
            System.out.println();
        }

        System.out.println();
        System.out.println("Max Gamma 2 = " + Collections.max(minMaxGamma));
        System.out.println("Min Gamma 1 = " + Collections.min(maxMinGamma));
        System.out.println("Reshenie igry = " + ((gamma1 + gamma2)/2));
        System.out.println("Delta = " + (gamma1 - gamma2));


        for (Map.Entry<Integer, Integer> entry:Sa.entrySet()) {
            System.out.printf("A%-7d", entry.getKey());
        }
        System.out.println();
        for (Map.Entry<Integer, Integer> entry:Sa.entrySet()) {
            System.out.printf("%d/8 \t",  entry.getValue());
        }
        System.out.println();
        for (Map.Entry<Integer, Integer> entry:Sb.entrySet()) {
            System.out.printf("B%-7d", entry.getKey());
        }
        System.out.println();
        for (Map.Entry<Integer, Integer> entry:Sb.entrySet()) {
            System.out.printf("%d/8 \t",  entry.getValue());
        }
        System.out.println();
    }
}

