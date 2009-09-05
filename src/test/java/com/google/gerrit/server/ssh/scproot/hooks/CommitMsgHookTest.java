begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Licensed under the Apache License, Version 2.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License.
end_comment

begin_comment
comment|// You may obtain a copy of the License at
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// limitations under the License.
end_comment

begin_package
DECL|package|com.google.gerrit.server.ssh.scproot.hooks
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ssh
operator|.
name|scproot
operator|.
name|hooks
package|;
end_package

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|dircache
operator|.
name|DirCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|dircache
operator|.
name|DirCacheBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|dircache
operator|.
name|DirCacheEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Commit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|FileMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|RefUpdate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|CommitMsgHookTest
specifier|public
class|class
name|CommitMsgHookTest
extends|extends
name|HookTestCase
block|{
DECL|field|SOB1
specifier|private
specifier|final
name|String
name|SOB1
init|=
literal|"Signed-off-by: J Author<ja@example.com>\n"
decl_stmt|;
DECL|field|SOB2
specifier|private
specifier|final
name|String
name|SOB2
init|=
literal|"Signed-off-by: J Committer<jc@example.com>\n"
decl_stmt|;
DECL|method|testEmptyMessages ()
specifier|public
name|void
name|testEmptyMessages
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Empty input must yield empty output so commit will abort.
comment|// Note we must consider different commit templates formats.
comment|//
name|hookDoesNotModify
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"  \n  "
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"#\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"# on branch master\n# Untracked files:\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"\n# on branch master\n# Untracked files:\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"\n\n# on branch master\n# Untracked files:\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"\n# on branch master\ndiff --git a/src b/src\n"
operator|+
literal|"new file mode 100644\nindex 0000000..c78b7f0\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangeIdAlreadySet ()
specifier|public
name|void
name|testChangeIdAlreadySet
parameter_list|()
throws|throws
name|Exception
block|{
comment|// If a Change-Id is already present in the footer, the hook must
comment|// not modify the message but instead must leave the identity alone.
comment|//
name|hookDoesNotModify
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: Iaeac9b4149291060228ef0154db2985a31111335\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"fix: this thing\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I388bdaf52ed05b55e62a22d0a20d2c1ae0d33e7e\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"fix-a-widget: this thing\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: Id3bc5359d768a6400450283e12bdfb6cd135ea4b\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"FIX: this thing\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I1b55098b5a2cce0b3f3da783dda50d5f79f873fa\n"
argument_list|)
expr_stmt|;
name|hookDoesNotModify
argument_list|(
literal|"Fix-A-Widget: this thing\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I4f4e2e1e8568ddc1509baecb8c1270a1fb4b6da7\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTimeAltersId ()
specifier|public
name|void
name|testTimeAltersId
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I7fc3876fee63c766a2063df97fbe04a2dddd8d7c\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
argument_list|)
argument_list|)
expr_stmt|;
name|tick
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I3251906b99dda598a58a6346d8126237ee1ea800\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
argument_list|)
argument_list|)
expr_stmt|;
name|tick
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I69adf9208d828f41a3d7e41afbca63aff37c0c5c\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFirstParentAltersId ()
specifier|public
name|void
name|testFirstParentAltersId
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I7fc3876fee63c766a2063df97fbe04a2dddd8d7c\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
argument_list|)
argument_list|)
expr_stmt|;
name|setHEAD
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I51e86482bde7f92028541aaf724d3a3f996e7ea2\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDirCacheAltersId ()
specifier|public
name|void
name|testDirCacheAltersId
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I7fc3876fee63c766a2063df97fbe04a2dddd8d7c\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DirCacheBuilder
name|builder
init|=
name|DirCache
operator|.
name|lock
argument_list|(
name|repository
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|file
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: If56597ea9759f23b070677ea6f064c60c38da631\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleLineMessages ()
specifier|public
name|void
name|testSingleLineMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I7fc3876fee63c766a2063df97fbe04a2dddd8d7c\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fix: this thing\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I0f13d0e6c739ca3ae399a05a93792e80feb97f37\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"fix: this thing\n"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fix-a-widget: this thing\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I1a1a0c751e4273d532e4046a501a612b9b8a775e\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"fix-a-widget: this thing\n"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FIX: this thing\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: If816d944c57d3893b60cf10c65931fead1290d97\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"FIX: this thing\n"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fix-A-Widget: this thing\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I3e18d00cbda2ba1f73aeb63ed8c7d57d7fd16c76\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"Fix-A-Widget: this thing\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiLineMessagesWithoutFooter ()
specifier|public
name|void
name|testMultiLineMessagesWithoutFooter
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"b\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: Id0b4f42d3d6fc1569595c9b97cb665e738486f5d\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
literal|"\n"
operator|+
literal|"b\n"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"b\nc\nd\ne\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I7d237b20058a0f46cc3f5fabc4a0476877289d75\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
literal|"\n"
operator|+
literal|"b\nc\nd\ne\n"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"b\nc\nd\ne\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"f\ng\nh\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I382e662f47bf164d6878b7fe61637873ab7fa4e8\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
literal|"\n"
operator|+
literal|"b\nc\nd\ne\n"
operator|+
literal|"\n"
operator|+
literal|"f\ng\nh\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleLineMessagesWithSignedOffBy ()
specifier|public
name|void
name|testSingleLineMessagesWithSignedOffBy
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I7fc3876fee63c766a2063df97fbe04a2dddd8d7c\n"
operator|+
comment|//
name|SOB1
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
literal|"\n"
operator|+
name|SOB1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I7fc3876fee63c766a2063df97fbe04a2dddd8d7c\n"
operator|+
comment|//
name|SOB1
operator|+
comment|//
name|SOB2
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
literal|"\n"
operator|+
name|SOB1
operator|+
name|SOB2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiLineMessagesWithSignedOffBy ()
specifier|public
name|void
name|testMultiLineMessagesWithSignedOffBy
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"b\nc\nd\ne\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"f\ng\nh\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I382e662f47bf164d6878b7fe61637873ab7fa4e8\n"
operator|+
comment|//
name|SOB1
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
literal|"\n"
operator|+
literal|"b\nc\nd\ne\n"
operator|+
literal|"\n"
operator|+
literal|"f\ng\nh\n"
operator|+
literal|"\n"
operator|+
name|SOB1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"b\nc\nd\ne\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"f\ng\nh\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I382e662f47bf164d6878b7fe61637873ab7fa4e8\n"
operator|+
comment|//
name|SOB1
operator|+
comment|//
name|SOB2
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"b\nc\nd\ne\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"f\ng\nh\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
name|SOB1
operator|+
comment|//
name|SOB2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"b: not a footer\nc\nd\ne\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"f\ng\nh\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I8869aabd44b3017cd55d2d7e0d546a03e3931ee2\n"
operator|+
comment|//
name|SOB1
operator|+
comment|//
name|SOB2
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"b: not a footer\nc\nd\ne\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"f\ng\nh\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
name|SOB1
operator|+
comment|//
name|SOB2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoteInMiddle ()
specifier|public
name|void
name|testNoteInMiddle
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"NOTE: This\n"
operator|+
comment|//
literal|"does not fix it.\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I988a127969a6ee5e58db546aab74fc46e66847f8\n"
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"NOTE: This\n"
operator|+
comment|//
literal|"does not fix it.\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testKernelStyleFooter ()
specifier|public
name|void
name|testKernelStyleFooter
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Change-Id: I1bd787f9e7590a2ac82b02c404c955ffb21877c4\n"
operator|+
comment|//
name|SOB1
operator|+
comment|//
literal|"[ja: Fixed\n"
operator|+
comment|//
literal|"     the indentation]\n"
operator|+
comment|//
name|SOB2
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
name|SOB1
operator|+
comment|//
literal|"[ja: Fixed\n"
operator|+
comment|//
literal|"     the indentation]\n"
operator|+
comment|//
name|SOB2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangeIdAfterBugOrIssue ()
specifier|public
name|void
name|testChangeIdAfterBugOrIssue
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Bug: 42\n"
operator|+
comment|//
literal|"Change-Id: I8c0321227c4324e670b9ae8cf40eccc87af21b1b\n"
operator|+
comment|//
name|SOB1
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Bug: 42\n"
operator|+
comment|//
name|SOB1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Issue: 42\n"
operator|+
comment|//
literal|"Change-Id: Ie66e07d89ae5b114c0975b49cf326e90331dd822\n"
operator|+
comment|//
name|SOB1
argument_list|,
comment|//
name|call
argument_list|(
literal|"a\n"
operator|+
comment|//
literal|"\n"
operator|+
comment|//
literal|"Issue: 42\n"
operator|+
comment|//
name|SOB1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|hookDoesNotModify (final String in)
specifier|private
name|void
name|hookDoesNotModify
parameter_list|(
specifier|final
name|String
name|in
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|in
argument_list|,
name|call
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|call (final String body)
specifier|private
name|String
name|call
parameter_list|(
specifier|final
name|String
name|body
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|File
name|tmp
init|=
name|write
argument_list|(
name|body
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|File
name|hook
init|=
name|getHook
argument_list|(
literal|"commit-msg"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runHook
argument_list|(
name|repository
argument_list|,
name|hook
argument_list|,
name|tmp
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|read
argument_list|(
name|tmp
argument_list|)
return|;
block|}
finally|finally
block|{
name|tmp
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|file (final String name)
specifier|private
name|DirCacheEntry
name|file
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DirCacheEntry
name|e
init|=
operator|new
name|DirCacheEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|e
operator|.
name|setFileMode
argument_list|(
name|FileMode
operator|.
name|REGULAR_FILE
argument_list|)
expr_stmt|;
name|e
operator|.
name|setObjectId
argument_list|(
name|writer
argument_list|()
operator|.
name|writeBlob
argument_list|(
name|Constants
operator|.
name|encode
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
DECL|method|setHEAD ()
specifier|private
name|void
name|setHEAD
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ObjectWriter
name|ow
init|=
name|writer
argument_list|()
decl_stmt|;
specifier|final
name|Commit
name|commit
init|=
operator|new
name|Commit
argument_list|(
name|repository
argument_list|)
decl_stmt|;
name|commit
operator|.
name|setTreeId
argument_list|(
name|DirCache
operator|.
name|newInCore
argument_list|()
operator|.
name|writeTree
argument_list|(
name|ow
argument_list|)
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setAuthor
argument_list|(
name|author
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setCommitter
argument_list|(
name|committer
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setMessage
argument_list|(
literal|"test\n"
argument_list|)
expr_stmt|;
specifier|final
name|ObjectId
name|commitId
init|=
name|ow
operator|.
name|writeCommit
argument_list|(
name|commit
argument_list|)
decl_stmt|;
specifier|final
name|RefUpdate
name|ref
init|=
name|repository
operator|.
name|updateRef
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|)
decl_stmt|;
name|ref
operator|.
name|setNewObjectId
argument_list|(
name|commitId
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|ref
operator|.
name|forceUpdate
argument_list|()
condition|)
block|{
case|case
name|NEW
case|:
case|case
name|FAST_FORWARD
case|:
case|case
name|FORCED
case|:
case|case
name|NO_CHANGE
case|:
break|break;
default|default:
name|fail
argument_list|(
name|Constants
operator|.
name|HEAD
operator|+
literal|" did not change: "
operator|+
name|ref
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writer ()
specifier|private
name|ObjectWriter
name|writer
parameter_list|()
block|{
return|return
operator|new
name|ObjectWriter
argument_list|(
name|repository
argument_list|)
return|;
block|}
block|}
end_class

end_unit

