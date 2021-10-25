# git常用命令
- 拉取远程分支：

	方式1：拉取远程分支并创建本地分支
		git checkout -b 本地分支名x origin/远程分支名x
		
		使用该方式会在本地新建分支x，并自动切换到该本地分支x。采用此种方法建立的本地分支会和远程分支建立映射关系。
	方式2：
		git fetch origin 远程分支名x:本地分支名x
		
		使用该方式会在本地新建分支x，但是不会自动切换到该本地分支x，需要手动checkout。采用此种方法建立的本地分支不会和远程分支建立映射关系。
- 远程分支强制覆盖本地分支
		git fetch --all
		git reset --hard origin/master (这里master要修改为对应的分支名)
		git pull

- 新建本地分支

	> $ git checkout -b iss53
	
	Switched to a new branch "iss53"
	
	它是下面两条命令的简写：
	
	> $ git branch iss53
	> 
	> $ git checkout iss53

- 删除远程分支

	假设你已经通过远程分支做完所有的工作了 - 也就是说你和你的协作者已经完成了一个特性并且将其合并到了远程仓库的 master 分支（或任何其他稳定代码分支）。 可以运行带有`--delete`选项的 `git push `命令来删除一个远程分支。 如果想要从服务器上删除 serverfix 分支，运行下面的命令：
	
	> $ git push origin --delete serverfix
	
	> `To https://github.com/schacon/simplegit`
	
	> `- [deleted]         serverfix`
	
	基本上这个命令做的只是从服务器上移除这个指针。 Git 服务器通常会保留数据一段时间直到垃圾回收运行，所以如果不小心删除掉了，通常是很容易恢复的。

- 新建远程分支
	
	* 新建一个本地分支(参见上文)
	* 把新建的本地分支push到远程服务器，远程分支与本地分支同名（当然可以随意起名）：
		> git push origin remote_branch_name:local_branch_name

- 删除本地分支
	
	> git branch -d hotfix

	如果有异常，则无法删除。

	> git branch -D hotfix

	强制性删除
- 推送本地分支

	> git push origin local_branch_name:romote_branch_name
	> git push origin local_branch_name

- 更改文件

	> git checkout

	撤回更改文件

	> git checkout -- "文件绝对路径"
	
- 删除 untracked files

	> git clean -f
	
	连 untracked 的目录也一起删掉
	> git clean -fd
	
	连 gitignore 的untrack 文件/目录也一起删掉 （慎用，一般这个是用来删掉编译出来的 .o之类的文件用的）
	> git clean -xfd
	
	在用上述 git clean 前，强烈建议加上 -n 参数来先看看会删掉哪些文件，防止重要文件被误删
	
	> git clean -nxfd
	> 
	> git clean -nf
	> 
	> git clean -nfd

- 取消冲突的merge

	 In Git 1.7.0 or later, to cancel a conflicting merge, use git reset --merge
	> git reset --merge

- 缓存当前的修改
	往堆栈推送一个新的储藏
	> git stash 
 
	或可以添加一些注释

	> git stash save 'message...'
	
	 查看现有的储藏

	> git stash list
	
	使用最近的储藏并尝试应用它
	> git stash apply
	
	或

	> git stash apply [–-index indexnum] [stash_id]git
	
	移除stash
	> git stash drop stash@{0}
	
- 查看某个文件的修改历史

	> git log --pretty=oneline 文件名

	如上所示，打印出来的就是针对"文件名"的所有的改动历史，每一行最前面的那一长串数字就是每次提交形成的哈希值，接下来使用git show即可显示具体的某次的改动的修改

	> git show 356f6def9d3fb7f3b9032ff5aa4b9110d4cca87e

- 查看某人的提交记录

   > git log --since=2017-05-21 --until=2017-06-20 --author="author-name"
   
	可以查找某一个作者所有的提交

- 撤销最近一次提交（commit），但保留代码修改(不保留代码修改则改用--hard)

	> git reset --soft HEAD^

- 合并某个分支上的单个commit到其他分支

	首先，用git log或GitX工具查看一下你想选择哪些commits进行合并，例如：

	比如，feature 分支上的commit 62ecb3 非常重要，它含有一个bug的修改，或其他人想访问的内容。无论什么原因，你现在只需要将62ecb3 合并到master，而不合并feature上的其他commits，所以我们用git cherry-pick命令来做：

	> git checkout master
	> 
	> git cherry-pick 62ecb3

	这样就好啦。现在62ecb3 就被合并到master分支，并在master中添加了commit（作为一个新的commit）。cherry-pick 和merge比较类似，如果git不能合并代码改动（比如遇到合并冲突），git需要你自己来解决冲突并手动添加commit。

- 合并某个分支上的一系列commits到其他分支

	在一些特性情况下，合并单个commit并不够，你需要合并一系列相连的commits。这种情况下就不要选择cherry-pick了，rebase 更适合。还以上例为例，假设你需要合并feature分支的commit 76cada ~62ecb3 到master分支。

	首先需要基于feature创建一个新的分支，并指明新分支的最后一个commit：

	> git checkout -b newbranch 62ecb3

	然后，rebase这个新分支的commit到master（--ontomaster）。76cada^ 指明你想从哪个特定的commit开始。

	> git rebase --onto master 76cada^
	
	得到的结果就是feature分支的commit 76cada ~62ecb3 都被合并到了master分支。
