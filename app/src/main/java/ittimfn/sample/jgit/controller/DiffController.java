package ittimfn.sample.jgit.controller;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import ittimfn.sample.jgit.Util;
import lombok.Data;

/**
 * 参考：https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowBranchDiff.java
 */
@Data
public class DiffController {

    private Logger logger = LogManager.getLogger();
    
    private String targetBranch;
    private String sourceBranch;

    private AbstractTreeIterator sourceTree;
    private AbstractTreeIterator targetTree;

    private Repository repository;

    public DiffController(Repository repository, String targetBranch, String sourceBranch) {
        this.repository = repository;
        this.targetBranch = targetBranch;
        this.sourceBranch = sourceBranch;
    }

    /**
     * 通常のdiff
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public List<DiffEntry> getDiff() throws IOException, GitAPIException {

        logger.info(repository);
        logger.info("{} -> {}", sourceBranch, targetBranch);

        return this.getDiffEntryList(repository, targetBranch, sourceBranch);
    }

    private List<DiffEntry> getDiffEntryList(Repository repository, String targetBranch, String sourceBranch) throws GitAPIException, IOException {
        AbstractTreeIterator target = prepareTreeParser(repository, targetBranch);
        AbstractTreeIterator source = prepareTreeParser(repository, sourceBranch);
        return new Git(repository).diff().setOldTree(target).setNewTree(source).call();
    }
    
    private AbstractTreeIterator prepareTreeParser(Repository repository, String branch) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        logger.info("branch : {}", branch);
        CreateBranchController.createBranch(repository, branch);
        Ref head = repository.exactRef(Util.branchToRef(branch));
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }
}
