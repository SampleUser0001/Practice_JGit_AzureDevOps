# Practice_JGit_AzureDevOps
JGitを使ってみる。リポジトリはAzure DevOpsを使う。

- [Practice\_JGit\_AzureDevOps](#practice_jgit_azuredevops)
  - [総評](#総評)
    - [CloneRepositoryController.java](#clonerepositorycontrollerjava)
  - [実行](#実行)
  - [参考](#参考)

## 総評

Javaでgitを実行したかったが、ドキュメントが足りずやりたいこと(※)ができなかった。（git clone + git commandすればいいという結論になった。）  
リポジトリのcloneだけはJGitを使ったほうが都合が良さそうなので、cloneのみドキュメントに残す。

※具体的には`git diff $target_branch...$source_branch`。

### CloneRepositoryController.java

``` java
package ittimfn.sample.jgit.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import lombok.Getter;

@Getter
public class CloneRepositoryController {

    private String repoUrl;
    private String user;
    private String token;

    private Path repoDir;
    private Repository repository;
    private Git git;

    public CloneRepositoryController(String url, String user, String token) throws IOException {
        this.user = user;
        this.token = token;
        this.repoDir = Files.createTempDirectory(null);
        this.repoUrl = url;
    }

    public void gitClone() throws IOException, InvalidRemoteException, TransportException, GitAPIException {

        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(this.user, this.token);
        this.git = Git.cloneRepository()
                      .setURI(this.repoUrl)
                      .setDirectory(this.repoDir.toFile())
                      .setCredentialsProvider(cp)
                      .setCloneAllBranches(true)
                      .call();

        this.repository = this.git.getRepository();
    }

    public void rmdir() throws IOException {
        this.repoDir.toFile().deleteOnExit();
    }

}

```

## 実行

``` bash
./gradlew test
```

実行結果は`logs/testlog.log`を参照。

## 参考

- [jgit-cookbook:centic9:Github](https://github.com/centic9/jgit-cookbook)
- [A2.3 付録 B: Gitをあなたのアプリケーションに組み込む - JGit](https://git-scm.com/book/ja/v2/%E4%BB%98%E9%8C%B2-B%3A-Git%E3%82%92%E3%81%82%E3%81%AA%E3%81%9F%E3%81%AE%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E3%81%AB%E7%B5%84%E3%81%BF%E8%BE%BC%E3%82%80-JGit)
    - 認証の設定方法