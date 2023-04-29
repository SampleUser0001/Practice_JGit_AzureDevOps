package ittimfn.sample.jgit.controller;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;

import ittimfn.sample.jgit.Util;

public interface CreateBranchController {
    public static void createBranch(Repository repository, String branch) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException, IOException {
        if(repository.exactRef(Util.branchToRef(branch)) == null) {
            // first we need to ensure that the remote branch is visible locally
            new Git(repository).branchCreate().setName(branch).setStartPoint(Util.branchToOrigin(branch)).call();
        }
    }
}
