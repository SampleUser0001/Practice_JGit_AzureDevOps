package ittimfn.sample.jgit.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import ittimfn.sample.jgit.App;
import ittimfn.sample.jgit.enums.AuthPropertiesEnum;

public class CloneRepositoryControllerTest {
    
    public CloneRepositoryController controller;
    private Logger logger = LogManager.getLogger();

    @BeforeEach
    public void setup() throws IOException {
        App.propertiesLoad();
        this.controller = new CloneRepositoryController(
            AuthPropertiesEnum.URL.getPropertiesValue(),
            AuthPropertiesEnum.USER.getPropertiesValue(),
            AuthPropertiesEnum.TOKEN.getPropertiesValue());
    }

    /**
     * cloneできる
     * @throws InvalidRemoteException
     * @throws TransportException
     * @throws IOException
     * @throws GitAPIException
     */
    @Test
    public void gitCloneTest() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        this.controller.gitClone();
        
        assertThat(this.controller.getRepository(), is(notNullValue()));
        assertThat(Files.exists(this.controller.getRepoDir()), is(true));
    }

    @Test
    public void loggingBranches() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        this.controller.gitClone();
        for(Ref ref : this.controller.getGit().branchList().call()) {
            logger.info(ref);
        }
    }

    @AfterEach
    public void close() throws IOException {
        this.controller.rmdir();
    }
}
