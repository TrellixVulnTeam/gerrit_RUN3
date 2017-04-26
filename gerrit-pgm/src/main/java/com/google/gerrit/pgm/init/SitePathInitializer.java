begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm.init
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|FileUtil
operator|.
name|chmod
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|die
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|extract
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|mkdir
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|savePublic
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|version
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|FileUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|ConsoleUI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitFlags
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitStep
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|Section
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|Section
operator|.
name|Factory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
operator|.
name|SitePaths
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
operator|.
name|EmailModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Binding
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|TypeLiteral
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Initialize (or upgrade) an existing site. */
end_comment

begin_class
DECL|class|SitePathInitializer
specifier|public
class|class
name|SitePathInitializer
block|{
DECL|field|ui
specifier|private
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|steps
specifier|private
specifier|final
name|List
argument_list|<
name|InitStep
argument_list|>
name|steps
decl_stmt|;
DECL|field|sectionFactory
specifier|private
specifier|final
name|Factory
name|sectionFactory
decl_stmt|;
DECL|field|secureStoreInitData
specifier|private
specifier|final
name|SecureStoreInitData
name|secureStoreInitData
decl_stmt|;
annotation|@
name|Inject
DECL|method|SitePathInitializer ( final Injector injector, final ConsoleUI ui, final InitFlags flags, final SitePaths site, final Section.Factory sectionFactory, @Nullable final SecureStoreInitData secureStoreInitData)
specifier|public
name|SitePathInitializer
parameter_list|(
specifier|final
name|Injector
name|injector
parameter_list|,
specifier|final
name|ConsoleUI
name|ui
parameter_list|,
specifier|final
name|InitFlags
name|flags
parameter_list|,
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|Section
operator|.
name|Factory
name|sectionFactory
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|SecureStoreInitData
name|secureStoreInitData
parameter_list|)
block|{
name|this
operator|.
name|ui
operator|=
name|ui
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|sectionFactory
operator|=
name|sectionFactory
expr_stmt|;
name|this
operator|.
name|secureStoreInitData
operator|=
name|secureStoreInitData
expr_stmt|;
name|this
operator|.
name|steps
operator|=
name|stepsOf
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|ui
operator|.
name|header
argument_list|(
literal|"Gerrit Code Review %s"
argument_list|,
name|version
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|site
operator|.
name|isNew
condition|)
block|{
if|if
condition|(
operator|!
name|ui
operator|.
name|yesno
argument_list|(
literal|true
argument_list|,
literal|"Create '%s'"
argument_list|,
name|site
operator|.
name|site_path
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"aborted by user"
argument_list|)
throw|;
block|}
name|FileUtil
operator|.
name|mkdirsOrDie
argument_list|(
name|site
operator|.
name|site_path
argument_list|,
literal|"Cannot make directory"
argument_list|)
expr_stmt|;
name|flags
operator|.
name|deleteOnFailure
operator|=
literal|true
expr_stmt|;
block|}
name|mkdir
argument_list|(
name|site
operator|.
name|bin_dir
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|site
operator|.
name|etc_dir
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|site
operator|.
name|lib_dir
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|site
operator|.
name|tmp_dir
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|site
operator|.
name|logs_dir
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|site
operator|.
name|mail_dir
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|site
operator|.
name|static_dir
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|site
operator|.
name|plugins_dir
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|site
operator|.
name|data_dir
argument_list|)
expr_stmt|;
for|for
control|(
name|InitStep
name|step
range|:
name|steps
control|)
block|{
if|if
condition|(
name|step
operator|instanceof
name|InitPlugins
operator|&&
name|flags
operator|.
name|skipPlugins
condition|)
block|{
continue|continue;
block|}
name|step
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|saveSecureStore
argument_list|()
expr_stmt|;
name|savePublic
argument_list|(
name|flags
operator|.
name|cfg
argument_list|)
expr_stmt|;
name|extract
argument_list|(
name|site
operator|.
name|gerrit_sh
argument_list|,
name|getClass
argument_list|()
argument_list|,
literal|"gerrit.sh"
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|0755
argument_list|,
name|site
operator|.
name|gerrit_sh
argument_list|)
expr_stmt|;
name|extract
argument_list|(
name|site
operator|.
name|gerrit_service
argument_list|,
name|getClass
argument_list|()
argument_list|,
literal|"gerrit.service"
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|0755
argument_list|,
name|site
operator|.
name|gerrit_service
argument_list|)
expr_stmt|;
name|extract
argument_list|(
name|site
operator|.
name|gerrit_service
argument_list|,
name|getClass
argument_list|()
argument_list|,
literal|"gerrit.socket"
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|0755
argument_list|,
name|site
operator|.
name|gerrit_socket
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|0700
argument_list|,
name|site
operator|.
name|tmp_dir
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"Abandoned.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"AbandonedHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"AddKey.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"ChangeFooter.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"ChangeFooterHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"ChangeSubject.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"Comment.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"CommentHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"CommentFooter.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"CommentFooterHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"DeleteReviewer.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"DeleteReviewerHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"DeleteVote.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"DeleteVoteHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"Footer.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"FooterHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"HeaderHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"Merged.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"MergedHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"NewChange.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"NewChangeHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"RegisterNewEmail.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"ReplacePatchSet.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"ReplacePatchSetHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"Restored.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"RestoredHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"Reverted.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"RevertedHtml.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"SetAssignee.soy"
argument_list|)
expr_stmt|;
name|extractMailExample
argument_list|(
literal|"SetAssigneeHtml.soy"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ui
operator|.
name|isBatch
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|postRun (Injector injector)
specifier|public
name|void
name|postRun
parameter_list|(
name|Injector
name|injector
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|InitStep
name|step
range|:
name|steps
control|)
block|{
if|if
condition|(
name|step
operator|instanceof
name|InitPlugins
operator|&&
name|flags
operator|.
name|skipPlugins
condition|)
block|{
continue|continue;
block|}
name|injector
operator|.
name|injectMembers
argument_list|(
name|step
argument_list|)
expr_stmt|;
name|step
operator|.
name|postRun
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|saveSecureStore ()
specifier|private
name|void
name|saveSecureStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|secureStoreInitData
operator|!=
literal|null
condition|)
block|{
name|Path
name|dst
init|=
name|site
operator|.
name|lib_dir
operator|.
name|resolve
argument_list|(
name|secureStoreInitData
operator|.
name|jarFile
operator|.
name|getFileName
argument_list|()
argument_list|)
decl_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|secureStoreInitData
operator|.
name|jarFile
argument_list|,
name|dst
argument_list|)
expr_stmt|;
name|Section
name|gerritSection
init|=
name|sectionFactory
operator|.
name|get
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|gerritSection
operator|.
name|set
argument_list|(
literal|"secureStoreClass"
argument_list|,
name|secureStoreInitData
operator|.
name|className
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|extractMailExample (String orig)
specifier|private
name|void
name|extractMailExample
parameter_list|(
name|String
name|orig
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|ex
init|=
name|site
operator|.
name|mail_dir
operator|.
name|resolve
argument_list|(
name|orig
operator|+
literal|".example"
argument_list|)
decl_stmt|;
name|extract
argument_list|(
name|ex
argument_list|,
name|EmailModule
operator|.
name|class
argument_list|,
name|orig
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|0444
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
DECL|method|stepsOf (final Injector injector)
specifier|private
specifier|static
name|List
argument_list|<
name|InitStep
argument_list|>
name|stepsOf
parameter_list|(
specifier|final
name|Injector
name|injector
parameter_list|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|InitStep
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Binding
argument_list|<
name|InitStep
argument_list|>
name|b
range|:
name|all
argument_list|(
name|injector
argument_list|)
control|)
block|{
name|r
operator|.
name|add
argument_list|(
name|b
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|all (final Injector injector)
specifier|private
specifier|static
name|List
argument_list|<
name|Binding
argument_list|<
name|InitStep
argument_list|>
argument_list|>
name|all
parameter_list|(
specifier|final
name|Injector
name|injector
parameter_list|)
block|{
return|return
name|injector
operator|.
name|findBindingsByType
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|InitStep
argument_list|>
argument_list|()
block|{}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

