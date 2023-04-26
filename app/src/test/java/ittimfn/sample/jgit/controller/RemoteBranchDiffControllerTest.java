package ittimfn.sample.jgit.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import ittimfn.sample.jgit.App;
import ittimfn.sample.jgit.controller.remote.RemoteBranchesDiffController;
import ittimfn.sample.jgit.enums.AuthPropertiesEnum;

public class RemoteBranchDiffControllerTest {
    private Logger logger = LogManager.getLogger();
    
    public RemoteBranchesDiffController controller;

    private Method getRemoteRepository;
    private Method getDiffEntryList;

    @BeforeEach
    public void setup() throws IOException, NoSuchMethodException, SecurityException {
        App.propertiesLoad();
        this.controller = new RemoteBranchesDiffController(null, null);

        getRemoteRepository = RemoteBranchesDiffController.class.getDeclaredMethod(
            "getRemoteRepository",
             String.class, String.class, String.class);
        getRemoteRepository.setAccessible(true);

        getDiffEntryList = RemoteBranchesDiffController.class.getDeclaredMethod(
            "getDiffEntryList",
             Repository.class, String.class, String.class);
        getDiffEntryList.setAccessible(true);
    }

    @Test
    public void getRepositoryTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String user = AuthPropertiesEnum.USER.getPropertiesValue();
        String url = AuthPropertiesEnum.URL.getPropertiesValue();

        logger.debug("USER : {}", user);
        logger.debug("URL : {}", url);

        Repository actural = (Repository) getRemoteRepository.invoke(
            this.controller,
            user,
            AuthPropertiesEnum.TOKEN.getPropertiesValue(),
            url
        );

        assertThat(actural, is(notNullValue()));        
    }

    @Test
    public void getDiffEntryListTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Repository repository = (Repository) getRemoteRepository.invoke(
            this.controller,
            AuthPropertiesEnum.USER.getPropertiesValue(),
            AuthPropertiesEnum.TOKEN.getPropertiesValue(),
            AuthPropertiesEnum.URL.getPropertiesValue()
        );

        String targetBranch = "refs/heads/develop";
        String sourceBranch = "refs/heads/feature/004_pull_request";
        List<DiffEntry> actualList = (List<DiffEntry>) getDiffEntryList.invoke(this.controller, repository, targetBranch, sourceBranch);
        
        actualList.forEach(System.out::println);
    }
}
