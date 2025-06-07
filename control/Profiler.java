package control;

import java.util.function.Function;

public class Profiler {

    public static long globalTime = 0;
    public static int nbExec = 0;

    public static double analyse(Function<Double, Double> oneMethod, double p){
        long start_time = timestamp();
        double res = oneMethod.apply(p);
        long time = timestamp() - start_time;
        globalTime += time;
        nbExec++;
        System.out.println("[Profiler] " + time + " ns");

        return res;
    }

    public static int[] analyse(Function<int[], int[]> oneMethod, int[] p){
        long start_time = timestamp();
        int[] res = oneMethod.apply(p);
        long time = timestamp() - start_time;
        globalTime += time;
        nbExec++;
        System.out.println("[Profiler] " + time + " ns");

        return res;
    }

    public static long getGlobalTime(){
        return globalTime;
    }

    public static int getNbExec(){
        return nbExec;
    }

    /**
     * Si clock0 est >0, retourne une chaîne de caractères
     * représentant la différence de temps depuis clock0.
     * @param clock0 instant initial
     * @return expression du temps écoulé depuis clock0
     */
    public static String timestamp(long clock0) {
        String result = null;

        if (clock0 > 0) {
            double elapsed = (System.nanoTime() - clock0) / 1e9;
            String unit = "s";
            if (elapsed < 1.0) {
                elapsed *= 1000.0;
                unit = "ms";
            }
            result = String.format("%.4g%s elapsed", elapsed, unit);
        }
        return result;
    }

    /**
     * retourne l'heure courante en ns.
     * @return
     */
    public static long timestamp() {
        return System.nanoTime();
    }
}
