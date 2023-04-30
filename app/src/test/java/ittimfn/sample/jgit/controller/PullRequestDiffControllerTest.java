package ittimfn.sample.jgit.controller;

import ittimfn.sample.jgit.App;
import ittimfn.sample.jgit.enums.AuthPropertiesEnum;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PullRequestDiffControllerTest {
    
    public CloneRepositoryController cloneRepository;
    public PullRequestDiffController controller;
    
    @BeforeEach
    public void setup() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        App.propertiesLoad();
        this.cloneRepository = new CloneRepositoryController(
            AuthPropertiesEnum.URL.getPropertiesValue(), 
            AuthPropertiesEnum.USER.getPropertiesValue(), 
            AuthPropertiesEnum.TOKEN.getPropertiesValue());
        this.cloneRepository.gitClone();

        this.controller = new PullRequestDiffController(this.cloneRepository.getRepository());
    }

    @Test
    public void diffPullRequest_Test() throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, IOException, InterruptedException, GitAPIException {
        List<String> actual = this.controller.diff("develop", "feature/004_pull_request");

        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is("M\tForPullRequest.md"));
    }

    @AfterEach
    public void shutdown() throws IOException {
        this.cloneRepository.rmdir();
    }

}
