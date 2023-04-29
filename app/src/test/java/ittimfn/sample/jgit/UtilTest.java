package ittimfn.sample.jgit;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class UtilTest {
    
    @Test
    public void refToBranch_Test() {
        String ref = "refs/heads/branch";
        assertThat(Util.refToBranch(ref), is(equalTo("branch")));
    }

    @Test
    public void originToBranch_Test() {
        String origin = "origin/branch";
        assertThat(Util.originToBranch(origin), is(equalTo("branch")));
    }

}
