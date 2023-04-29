package ittimfn.sample.jgit.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import ittimfn.sample.jgit.App;
import ittimfn.sample.jgit.enums.AuthPropertiesEnum;

public class BranchDiffControllerTest {
    private Logger logger = LogManager.getLogger();
    
    public BranchesDiffController controller;

    private CloneRepositoryController gitCloneController;

    private Method getRemoteRepository;
    private Method prepareTreeParser;
    private Method getDiffEntryList;
    

    @BeforeEach
    public void setup() throws IOException, NoSuchMethodException, SecurityException, InvalidRemoteException, TransportException, GitAPIException {
        App.propertiesLoad();
        this.gitCloneController = new CloneRepositoryController(
                AuthPropertiesEnum.URL.getPropertiesValue(),
                AuthPropertiesEnum.USER.getPropertiesValue(),
                AuthPropertiesEnum.TOKEN.getPropertiesValue());
        this.gitCloneController.gitClone();
        this.controller = new BranchesDiffController(
            this.gitCloneController.getRepository(), null, null);

        prepareTreeParser = BranchesDiffController.class.getDeclaredMethod(
            "prepareTreeParser",
            Repository.class, String.class
        );
        prepareTreeParser.setAccessible(true);

        getDiffEntryList = BranchesDiffController.class.getDeclaredMethod(
            "getDiffEntryList",
             Repository.class, String.class, String.class);
        getDiffEntryList.setAccessible(true);
    }

    @Test
    public void prepareTreeParser_callable() throws IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        String branch = "develop";
        prepareTreeParser.invoke(
            this.controller,
            this.gitCloneController.getRepository(),
            branch);

    }

    @Test
    public void getDiffEntryListTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String targetBranch = "develop";
        String sourceBranch = "feature/004_pull_request";
        List<DiffEntry> actualList = (List<DiffEntry>) getDiffEntryList.invoke(
            this.controller, this.gitCloneController.getRepository(), targetBranch, sourceBranch);
        
        logger.info("actualList.size() : {}", actualList.size());
        actualList.forEach(diff -> {
            logger.info("changeType: {}, newPath : {}, oldPath : {}", diff.getChangeType(), diff.getNewPath(), diff.getOldPath());

        });
    }

    @AfterEach
    public void shutdown() throws IOException {
        this.gitCloneController.rmdir();
    }
}
