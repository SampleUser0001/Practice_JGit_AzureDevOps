package ittimfn.sample.jgit.controller.remote;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import ittimfn.sample.jgit.enums.AuthPropertiesEnum;

import java.util.Collection;
import java.util.Map;

/**
 * リモートリポジトリのブランチを取得する。
 * 参考:https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ListRemoteRepository.java
 */
public class ListBranchesController {

    private Logger logger = LogManager.getLogger();

    public ListBranchesController() {}
    
    public void print() throws InvalidRemoteException, TransportException, GitAPIException {

        final String USER = AuthPropertiesEnum.USER.getPropertiesValue();
        final String TOKEN = AuthPropertiesEnum.TOKEN.getPropertiesValue();
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(USER, TOKEN);

        // then clone
        final String URL = AuthPropertiesEnum.URL.getPropertiesValue();

        logger.info("Repository URL : {}", URL);
        Collection<Ref> refs = Git.lsRemoteRepository()
                .setHeads(true)
                .setTags(true)
                .setRemote(URL)
                .setCredentialsProvider(cp)
                .call();

        for (Ref ref : refs) {
            logger.info("Ref: {}", ref);
        }

        final Map<String, Ref> map = Git.lsRemoteRepository()
                .setHeads(true)
                .setTags(true)
                .setRemote(URL)
                .setCredentialsProvider(cp)
                .callAsMap();

        logger.info("As map");
        for (Map.Entry<String, Ref> entry : map.entrySet()) {
            logger.info("Key: {}, Ref: {}", entry.getKey(), entry.getValue());
        }

        refs = Git.lsRemoteRepository()
                  .setRemote(URL)
                  .setCredentialsProvider(cp)
                  .call();

        logger.info("All refs");
        for (Ref ref : refs) {
            logger.info("Ref: {}", ref);
        }
    }
}
