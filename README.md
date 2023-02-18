# Gitlet: A Version Control Syatem

Gitlet is Version Control System which mimics some of the basic features of Git from a concept to an amazing product of backup system.
In this project I applied file serialization techniques to java objects so that to work with files and directories that persist the state of the Gitlet program across multiple executions.


A version-control system is essentially a backup system for related collections of files so that to save versions of any project or file periodically. In that case if at some later point in time someone accidentally messes up heir code, then they can restore the source to a previously committed version. 

In this project the main functionality that Gitlet supports is:

1. Saving the contents of entire directories of files. In Gitlet, this is called committing, and the saved contents themselves are called commits.

2. Restoring a version of one or more files or entire commits. In Gitlet, this is called checking out those files or that commit.

3. Viewing the history of your backups. In Gitlet, you view this history in something called the log.

4. Maintaining related sequences of commits, called branches.

5. Merging changes made in one branch into another.
