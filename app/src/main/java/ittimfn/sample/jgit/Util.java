package ittimfn.sample.jgit;

public class Util {
    
    private static final String REFS = "refs/heads/";
    private static final String ORIGIN = "origin/";

    public static String refToBranch(String ref) {
        return ref.substring(REFS.length()).trim();
    }

    public static String originToBranch(String origin) {
        return origin.substring(ORIGIN.length()).trim();
    }

    public static String branchToRef(String branch) {
        return REFS + branch;
    }

    public static String branchToOrigin(String branch) {
        return ORIGIN + branch;
    }
    private Util(){}
}
