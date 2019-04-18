begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
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
name|server
operator|.
name|notedb
operator|.
name|ReviewerStateInternal
operator|.
name|CC
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
name|server
operator|.
name|notedb
operator|.
name|ReviewerStateInternal
operator|.
name|REVIEWER
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
name|FooterConstants
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
name|extensions
operator|.
name|restapi
operator|.
name|UnprocessableEntityException
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
name|reviewdb
operator|.
name|client
operator|.
name|Account
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
name|ReviewerSet
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
name|account
operator|.
name|AccountResolver
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|ConfigInvalidException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|FooterKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|FooterLine
import|;
end_import

begin_class
DECL|class|MailUtil
specifier|public
class|class
name|MailUtil
block|{
DECL|method|getRecipientsFromFooters ( AccountResolver accountResolver, List<FooterLine> footerLines)
specifier|public
specifier|static
name|MailRecipients
name|getRecipientsFromFooters
parameter_list|(
name|AccountResolver
name|accountResolver
parameter_list|,
name|List
argument_list|<
name|FooterLine
argument_list|>
name|footerLines
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|MailRecipients
name|recipients
init|=
operator|new
name|MailRecipients
argument_list|()
decl_stmt|;
for|for
control|(
name|FooterLine
name|footerLine
range|:
name|footerLines
control|)
block|{
try|try
block|{
if|if
condition|(
name|isReviewer
argument_list|(
name|footerLine
argument_list|)
condition|)
block|{
name|recipients
operator|.
name|reviewers
operator|.
name|add
argument_list|(
name|toAccountId
argument_list|(
name|accountResolver
argument_list|,
name|footerLine
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|footerLine
operator|.
name|matches
argument_list|(
name|FooterKey
operator|.
name|CC
argument_list|)
condition|)
block|{
name|recipients
operator|.
name|cc
operator|.
name|add
argument_list|(
name|toAccountId
argument_list|(
name|accountResolver
argument_list|,
name|footerLine
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnprocessableEntityException
name|e
parameter_list|)
block|{
continue|continue;
block|}
block|}
return|return
name|recipients
return|;
block|}
DECL|method|getRecipientsFromReviewers (ReviewerSet reviewers)
specifier|public
specifier|static
name|MailRecipients
name|getRecipientsFromReviewers
parameter_list|(
name|ReviewerSet
name|reviewers
parameter_list|)
block|{
name|MailRecipients
name|recipients
init|=
operator|new
name|MailRecipients
argument_list|()
decl_stmt|;
name|recipients
operator|.
name|reviewers
operator|.
name|addAll
argument_list|(
name|reviewers
operator|.
name|byState
argument_list|(
name|REVIEWER
argument_list|)
argument_list|)
expr_stmt|;
name|recipients
operator|.
name|cc
operator|.
name|addAll
argument_list|(
name|reviewers
operator|.
name|byState
argument_list|(
name|CC
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|recipients
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|toAccountId (AccountResolver accountResolver, String nameOrEmail)
specifier|private
specifier|static
name|Account
operator|.
name|Id
name|toAccountId
parameter_list|(
name|AccountResolver
name|accountResolver
parameter_list|,
name|String
name|nameOrEmail
parameter_list|)
throws|throws
name|UnprocessableEntityException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
return|return
name|accountResolver
operator|.
name|resolveByNameOrEmail
argument_list|(
name|nameOrEmail
argument_list|)
operator|.
name|asUnique
argument_list|()
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|isReviewer (FooterLine candidateFooterLine)
specifier|private
specifier|static
name|boolean
name|isReviewer
parameter_list|(
name|FooterLine
name|candidateFooterLine
parameter_list|)
block|{
return|return
name|candidateFooterLine
operator|.
name|matches
argument_list|(
name|FooterKey
operator|.
name|SIGNED_OFF_BY
argument_list|)
operator|||
name|candidateFooterLine
operator|.
name|matches
argument_list|(
name|FooterKey
operator|.
name|ACKED_BY
argument_list|)
operator|||
name|candidateFooterLine
operator|.
name|matches
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_BY
argument_list|)
operator|||
name|candidateFooterLine
operator|.
name|matches
argument_list|(
name|FooterConstants
operator|.
name|TESTED_BY
argument_list|)
return|;
block|}
DECL|class|MailRecipients
specifier|public
specifier|static
class|class
name|MailRecipients
block|{
DECL|field|reviewers
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
decl_stmt|;
DECL|field|cc
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|cc
decl_stmt|;
DECL|method|MailRecipients ()
specifier|public
name|MailRecipients
parameter_list|()
block|{
name|this
operator|.
name|reviewers
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|cc
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|MailRecipients (Set<Account.Id> reviewers, Set<Account.Id> cc)
specifier|public
name|MailRecipients
parameter_list|(
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|cc
parameter_list|)
block|{
name|this
operator|.
name|reviewers
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|reviewers
argument_list|)
expr_stmt|;
name|this
operator|.
name|cc
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
DECL|method|add (MailRecipients recipients)
specifier|public
name|void
name|add
parameter_list|(
name|MailRecipients
name|recipients
parameter_list|)
block|{
name|reviewers
operator|.
name|addAll
argument_list|(
name|recipients
operator|.
name|reviewers
argument_list|)
expr_stmt|;
name|cc
operator|.
name|addAll
argument_list|(
name|recipients
operator|.
name|cc
argument_list|)
expr_stmt|;
block|}
DECL|method|remove (Account.Id toRemove)
specifier|public
name|void
name|remove
parameter_list|(
name|Account
operator|.
name|Id
name|toRemove
parameter_list|)
block|{
name|reviewers
operator|.
name|remove
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|cc
operator|.
name|remove
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
block|}
DECL|method|getReviewers ()
specifier|public
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getReviewers
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|reviewers
argument_list|)
return|;
block|}
DECL|method|getCcOnly ()
specifier|public
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getCcOnly
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|cc
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|this
operator|.
name|cc
argument_list|)
decl_stmt|;
name|cc
operator|.
name|removeAll
argument_list|(
name|reviewers
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|cc
argument_list|)
return|;
block|}
DECL|method|getAll ()
specifier|public
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getAll
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|all
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|reviewers
operator|.
name|size
argument_list|()
operator|+
name|cc
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|reviewers
argument_list|)
expr_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|cc
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|all
argument_list|)
return|;
block|}
block|}
comment|/** allow wildcard matching for {@code domains} */
DECL|method|glob (String[] domains)
specifier|public
specifier|static
name|Pattern
name|glob
parameter_list|(
name|String
index|[]
name|domains
parameter_list|)
block|{
comment|// if domains is not set, match anything
if|if
condition|(
name|domains
operator|==
literal|null
operator|||
name|domains
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*"
argument_list|)
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|domain
range|:
name|domains
control|)
block|{
name|String
name|quoted
init|=
literal|"\\Q"
operator|+
name|domain
operator|.
name|replace
argument_list|(
literal|"\\E"
argument_list|,
literal|"\\E\\\\E\\Q"
argument_list|)
operator|+
literal|"\\E|"
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|quoted
operator|.
name|replace
argument_list|(
literal|"*"
argument_list|,
literal|"\\E.*\\Q"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|sb
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

