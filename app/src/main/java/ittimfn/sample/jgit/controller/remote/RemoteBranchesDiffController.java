package ittimfn.sample.jgit.controller.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import ittimfn.sample.jgit.enums.AuthPropertiesEnum;
import lombok.Data;

/**
 * 参考：https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowBranchDiff.java
 */
@Data
public class RemoteBranchesDiffController {

    private Logger logger = LogManager.getLogger();
    
    private String targetBranch;
    private String sourceBranch;

    private AbstractTreeIterator sourceTree;
    private AbstractTreeIterator targetTree;

    private Repository repository;

    public RemoteBranchesDiffController(String targetBranch, String sourceBranch) {
        this.targetBranch = targetBranch;
        this.sourceBranch = sourceBranch;
    }

    public List<DiffEntry> getDiff() throws IOException, GitAPIException {
        this.repository = this.getRemoteRepository(
            AuthPropertiesEnum.USER.getPropertiesValue(),
            AuthPropertiesEnum.TOKEN.getPropertiesValue(),
            AuthPropertiesEnum.URL.getPropertiesValue());

        logger.info(repository);
        logger.info("{} -> {}", sourceBranch, targetBranch);

        return this.getDiffEntryList(repository, targetBranch, sourceBranch);

    }

    private Repository getRemoteRepository(String user, String token, String URL) {
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(user, token);

        return Git.lsRemoteRepository()
                  .setHeads(true)
                  .setTags(true)
                  .setRemote(URL)
                  .setCredentialsProvider(cp)
                  .getRepository();
    }

    private List<DiffEntry> getDiffEntryList(Repository repository, String targetBranch, String sourceBranch) throws GitAPIException, IOException {
        AbstractTreeIterator source = prepareTreeParser(repository, sourceBranch);
        AbstractTreeIterator target = prepareTreeParser(repository, targetBranch);
        return new Git(repository).diff().setOldTree(target).setNewTree(source).call();
    }
    
    private AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        Ref head = repository.exactRef(ref);
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
