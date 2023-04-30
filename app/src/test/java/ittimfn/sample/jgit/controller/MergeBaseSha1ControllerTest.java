package ittimfn.sample.jgit.controller;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ittimfn.sample.jgit.App;
import ittimfn.sample.jgit.enums.AuthPropertiesEnum;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;


public class MergeBaseSha1ControllerTest {

    public CloneRepositoryController cloneRepository;
    public MergeBaseSha1Controller controller;

    @BeforeEach
    public void setup() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        App.propertiesLoad();
        this.cloneRepository = new CloneRepositoryController(
            AuthPropertiesEnum.URL.getPropertiesValue(), 
            AuthPropertiesEnum.USER.getPropertiesValue(), 
            AuthPropertiesEnum.TOKEN.getPropertiesValue());
        this.cloneRepository.gitClone();

        this.controller = new MergeBaseSha1Controller(this.cloneRepository.getRepository());
    }

    @Test
    public void getMergeBaseSha1_Test() throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, IOException, GitAPIException, InterruptedException {
        String actual = this.controller.getMergeBaseSha1("develop", "feature/004_pull_request");
        String sha1 = "89f7a3bc23136f4089cc839bec65ab5ccbd5b204";
        assertThat(actual, is(equalTo(sha1)));
    }

    @AfterEach
    public void shutdown() throws IOException {
        this.cloneRepository.rmdir();
    }
}
