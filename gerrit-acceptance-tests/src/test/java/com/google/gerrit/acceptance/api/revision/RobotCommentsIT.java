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
DECL|package|com.google.gerrit.acceptance.api.revision
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
operator|.
name|revision
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|TruthJUnit
operator|.
name|assume
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
name|acceptance
operator|.
name|PushOneCommit
operator|.
name|FILE_NAME
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
name|acceptance
operator|.
name|api
operator|.
name|revision
operator|.
name|RobotCommentInfoSubject
operator|.
name|assertThatList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|PushOneCommit
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
name|api
operator|.
name|changes
operator|.
name|ReviewInput
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
name|api
operator|.
name|changes
operator|.
name|ReviewInput
operator|.
name|RobotCommentInput
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
name|client
operator|.
name|Comment
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
name|common
operator|.
name|FixReplacementInfo
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
name|common
operator|.
name|FixSuggestionInfo
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
name|common
operator|.
name|RobotCommentInfo
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
name|BadRequestException
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
name|MethodNotAllowedException
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
name|RestApiException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashMap
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
name|Map
import|;
end_import

begin_class
DECL|class|RobotCommentsIT
specifier|public
class|class
name|RobotCommentsIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|changeId
specifier|private
name|String
name|changeId
decl_stmt|;
DECL|field|fixReplacementInfo
specifier|private
name|FixReplacementInfo
name|fixReplacementInfo
decl_stmt|;
DECL|field|fixSuggestionInfo
specifier|private
name|FixSuggestionInfo
name|fixSuggestionInfo
decl_stmt|;
DECL|field|withFixRobotCommentInput
specifier|private
name|RobotCommentInput
name|withFixRobotCommentInput
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|changeResult
init|=
name|createChange
argument_list|()
decl_stmt|;
name|changeId
operator|=
name|changeResult
operator|.
name|getChangeId
argument_list|()
expr_stmt|;
name|fixReplacementInfo
operator|=
name|createFixReplacementInfo
argument_list|()
expr_stmt|;
name|fixSuggestionInfo
operator|=
name|createFixSuggestionInfo
argument_list|(
name|fixReplacementInfo
argument_list|)
expr_stmt|;
name|withFixRobotCommentInput
operator|=
name|createRobotCommentInput
argument_list|(
name|fixSuggestionInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|retrievingRobotCommentsBeforeAddingAnyDoesNotRaiseAnException ()
specifier|public
name|void
name|retrievingRobotCommentsBeforeAddingAnyDoesNotRaiseAnException
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
argument_list|>
name|robotComments
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|robotComments
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|robotComments
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|robotComments
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addedRobotCommentsCanBeRetrieved ()
specifier|public
name|void
name|addedRobotCommentsCanBeRetrieved
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|RobotCommentInput
name|in
init|=
name|createRobotCommentInput
argument_list|()
decl_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
argument_list|>
name|out
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|robotComments
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|out
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|RobotCommentInfo
name|comment
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|out
operator|.
name|get
argument_list|(
name|in
operator|.
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertRobotComment
argument_list|(
name|comment
argument_list|,
name|in
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|robotCommentsCanBeRetrievedAsList ()
specifier|public
name|void
name|robotCommentsCanBeRetrievedAsList
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|RobotCommentInput
name|robotCommentInput
init|=
name|createRobotCommentInput
argument_list|()
decl_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|robotCommentInput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|robotCommentInfos
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|robotCommentsAsList
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|robotCommentInfos
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|RobotCommentInfo
name|robotCommentInfo
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|robotCommentInfos
argument_list|)
decl_stmt|;
name|assertRobotComment
argument_list|(
name|robotCommentInfo
argument_list|,
name|robotCommentInput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|specificRobotCommentCanBeRetrieved ()
specifier|public
name|void
name|specificRobotCommentCanBeRetrieved
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|RobotCommentInput
name|robotCommentInput
init|=
name|createRobotCommentInput
argument_list|()
decl_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|robotCommentInput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|robotCommentInfos
init|=
name|getRobotComments
argument_list|()
decl_stmt|;
name|RobotCommentInfo
name|robotCommentInfo
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|robotCommentInfos
argument_list|)
decl_stmt|;
name|RobotCommentInfo
name|specificRobotCommentInfo
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|robotComment
argument_list|(
name|robotCommentInfo
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertRobotComment
argument_list|(
name|specificRobotCommentInfo
argument_list|,
name|robotCommentInput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|robotCommentWithoutOptionalFieldsCanBeAdded ()
specifier|public
name|void
name|robotCommentWithoutOptionalFieldsCanBeAdded
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|RobotCommentInput
name|in
init|=
name|createRobotCommentInputWithMandatoryFields
argument_list|()
decl_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
argument_list|>
name|out
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|robotComments
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|out
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|RobotCommentInfo
name|comment
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|out
operator|.
name|get
argument_list|(
name|in
operator|.
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertRobotComment
argument_list|(
name|comment
argument_list|,
name|in
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addedFixSuggestionCanBeRetrieved ()
specifier|public
name|void
name|addedFixSuggestionCanBeRetrieved
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|robotCommentInfos
init|=
name|getRobotComments
argument_list|()
decl_stmt|;
name|assertThatList
argument_list|(
name|robotCommentInfos
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|onlyFixSuggestion
argument_list|()
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|fixIdIsGeneratedForFixSuggestion ()
specifier|public
name|void
name|fixIdIsGeneratedForFixSuggestion
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|robotCommentInfos
init|=
name|getRobotComments
argument_list|()
decl_stmt|;
name|assertThatList
argument_list|(
name|robotCommentInfos
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|onlyFixSuggestion
argument_list|()
operator|.
name|fixId
argument_list|()
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|assertThatList
argument_list|(
name|robotCommentInfos
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|onlyFixSuggestion
argument_list|()
operator|.
name|fixId
argument_list|()
operator|.
name|isNotEqualTo
argument_list|(
name|fixSuggestionInfo
operator|.
name|fixId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|descriptionOfFixSuggestionIsAcceptedAsIs ()
specifier|public
name|void
name|descriptionOfFixSuggestionIsAcceptedAsIs
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|robotCommentInfos
init|=
name|getRobotComments
argument_list|()
decl_stmt|;
name|assertThatList
argument_list|(
name|robotCommentInfos
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|onlyFixSuggestion
argument_list|()
operator|.
name|description
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|fixSuggestionInfo
operator|.
name|description
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|descriptionOfFixSuggestionIsMandatory ()
specifier|public
name|void
name|descriptionOfFixSuggestionIsMandatory
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|fixSuggestionInfo
operator|.
name|description
operator|=
literal|null
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"A description is required for the "
operator|+
literal|"suggested fix of the robot comment on %s"
argument_list|,
name|withFixRobotCommentInput
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addedFixReplacementCanBeRetrieved ()
specifier|public
name|void
name|addedFixReplacementCanBeRetrieved
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|robotCommentInfos
init|=
name|getRobotComments
argument_list|()
decl_stmt|;
name|assertThatList
argument_list|(
name|robotCommentInfos
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|onlyFixSuggestion
argument_list|()
operator|.
name|onlyReplacement
argument_list|()
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|fixReplacementsAreMandatory ()
specifier|public
name|void
name|fixReplacementsAreMandatory
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|fixSuggestionInfo
operator|.
name|replacements
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"At least one replacement is required"
operator|+
literal|" for the suggested fix of the robot comment on %s"
argument_list|,
name|withFixRobotCommentInput
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|rangeOfFixReplacementIsAcceptedAsIs ()
specifier|public
name|void
name|rangeOfFixReplacementIsAcceptedAsIs
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|robotCommentInfos
init|=
name|getRobotComments
argument_list|()
decl_stmt|;
name|assertThatList
argument_list|(
name|robotCommentInfos
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|onlyFixSuggestion
argument_list|()
operator|.
name|onlyReplacement
argument_list|()
operator|.
name|range
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|fixReplacementInfo
operator|.
name|range
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|rangeOfFixReplacementIsMandatory ()
specifier|public
name|void
name|rangeOfFixReplacementIsMandatory
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|fixReplacementInfo
operator|.
name|range
operator|=
literal|null
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"A range must be given for the "
operator|+
literal|"replacement of the robot comment on %s"
argument_list|,
name|withFixRobotCommentInput
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|rangeOfFixReplacementNeedsToBeValid ()
specifier|public
name|void
name|rangeOfFixReplacementNeedsToBeValid
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|fixReplacementInfo
operator|.
name|range
operator|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|9
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Range (13:9 - 5:10) is not "
operator|+
literal|"valid for the replacement of the robot comment on %s"
argument_list|,
name|withFixRobotCommentInput
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replacementStringOfFixReplacementIsAcceptedAsIs ()
specifier|public
name|void
name|replacementStringOfFixReplacementIsAcceptedAsIs
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|robotCommentInfos
init|=
name|getRobotComments
argument_list|()
decl_stmt|;
name|assertThatList
argument_list|(
name|robotCommentInfos
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|onlyFixSuggestion
argument_list|()
operator|.
name|onlyReplacement
argument_list|()
operator|.
name|replacement
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|fixReplacementInfo
operator|.
name|replacement
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replacementStringOfFixReplacementIsMandatory ()
specifier|public
name|void
name|replacementStringOfFixReplacementIsMandatory
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|fixReplacementInfo
operator|.
name|replacement
operator|=
literal|null
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"A content for replacement must be "
operator|+
literal|"indicated for the replacement of the robot comment on %s"
argument_list|,
name|withFixRobotCommentInput
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|addRobotComment
argument_list|(
name|changeId
argument_list|,
name|withFixRobotCommentInput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|robotCommentsNotSupportedWithoutNoteDb ()
specifier|public
name|void
name|robotCommentsNotSupportedWithoutNoteDb
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|enabled
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|RobotCommentInput
name|in
init|=
name|createRobotCommentInput
argument_list|()
decl_stmt|;
name|ReviewInput
name|reviewInput
init|=
operator|new
name|ReviewInput
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RobotCommentInput
argument_list|>
argument_list|>
name|robotComments
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|robotComments
operator|.
name|put
argument_list|(
name|FILE_NAME
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|reviewInput
operator|.
name|robotComments
operator|=
name|robotComments
expr_stmt|;
name|reviewInput
operator|.
name|message
operator|=
literal|"comment test"
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|MethodNotAllowedException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"robot comments not supported"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
block|}
DECL|method|createRobotCommentInputWithMandatoryFields ()
specifier|private
name|RobotCommentInput
name|createRobotCommentInputWithMandatoryFields
parameter_list|()
block|{
name|RobotCommentInput
name|in
init|=
operator|new
name|RobotCommentInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|robotId
operator|=
literal|"happyRobot"
expr_stmt|;
name|in
operator|.
name|robotRunId
operator|=
literal|"1"
expr_stmt|;
name|in
operator|.
name|line
operator|=
literal|1
expr_stmt|;
name|in
operator|.
name|message
operator|=
literal|"nit: trailing whitespace"
expr_stmt|;
name|in
operator|.
name|path
operator|=
name|FILE_NAME
expr_stmt|;
return|return
name|in
return|;
block|}
DECL|method|createRobotCommentInput ( FixSuggestionInfo... fixSuggestionInfos)
specifier|private
name|RobotCommentInput
name|createRobotCommentInput
parameter_list|(
name|FixSuggestionInfo
modifier|...
name|fixSuggestionInfos
parameter_list|)
block|{
name|RobotCommentInput
name|in
init|=
name|createRobotCommentInputWithMandatoryFields
argument_list|()
decl_stmt|;
name|in
operator|.
name|url
operator|=
literal|"http://www.happy-robot.com"
expr_stmt|;
name|in
operator|.
name|properties
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|in
operator|.
name|properties
operator|.
name|put
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|in
operator|.
name|properties
operator|.
name|put
argument_list|(
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|in
operator|.
name|fixSuggestions
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|fixSuggestionInfos
argument_list|)
expr_stmt|;
return|return
name|in
return|;
block|}
DECL|method|createFixSuggestionInfo ( FixReplacementInfo... fixReplacementInfos)
specifier|private
name|FixSuggestionInfo
name|createFixSuggestionInfo
parameter_list|(
name|FixReplacementInfo
modifier|...
name|fixReplacementInfos
parameter_list|)
block|{
name|FixSuggestionInfo
name|newFixSuggestionInfo
init|=
operator|new
name|FixSuggestionInfo
argument_list|()
decl_stmt|;
name|newFixSuggestionInfo
operator|.
name|fixId
operator|=
literal|"An ID which must be overwritten."
expr_stmt|;
name|newFixSuggestionInfo
operator|.
name|description
operator|=
literal|"A description for a suggested fix."
expr_stmt|;
name|newFixSuggestionInfo
operator|.
name|replacements
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|fixReplacementInfos
argument_list|)
expr_stmt|;
return|return
name|newFixSuggestionInfo
return|;
block|}
DECL|method|createFixReplacementInfo ()
specifier|private
name|FixReplacementInfo
name|createFixReplacementInfo
parameter_list|()
block|{
name|FixReplacementInfo
name|newFixReplacementInfo
init|=
operator|new
name|FixReplacementInfo
argument_list|()
decl_stmt|;
name|newFixReplacementInfo
operator|.
name|replacement
operator|=
literal|"some replacement code"
expr_stmt|;
name|newFixReplacementInfo
operator|.
name|range
operator|=
name|createRange
argument_list|(
literal|3
argument_list|,
literal|12
argument_list|,
literal|15
argument_list|,
literal|4
argument_list|)
expr_stmt|;
return|return
name|newFixReplacementInfo
return|;
block|}
DECL|method|createRange (int startLine, int startCharacter, int endLine, int endCharacter)
specifier|private
name|Comment
operator|.
name|Range
name|createRange
parameter_list|(
name|int
name|startLine
parameter_list|,
name|int
name|startCharacter
parameter_list|,
name|int
name|endLine
parameter_list|,
name|int
name|endCharacter
parameter_list|)
block|{
name|Comment
operator|.
name|Range
name|range
init|=
operator|new
name|Comment
operator|.
name|Range
argument_list|()
decl_stmt|;
name|range
operator|.
name|startLine
operator|=
name|startLine
expr_stmt|;
name|range
operator|.
name|startCharacter
operator|=
name|startCharacter
expr_stmt|;
name|range
operator|.
name|endLine
operator|=
name|endLine
expr_stmt|;
name|range
operator|.
name|endCharacter
operator|=
name|endCharacter
expr_stmt|;
return|return
name|range
return|;
block|}
DECL|method|addRobotComment (String targetChangeId, RobotCommentInput robotCommentInput)
specifier|private
name|void
name|addRobotComment
parameter_list|(
name|String
name|targetChangeId
parameter_list|,
name|RobotCommentInput
name|robotCommentInput
parameter_list|)
throws|throws
name|Exception
block|{
name|ReviewInput
name|reviewInput
init|=
operator|new
name|ReviewInput
argument_list|()
decl_stmt|;
name|reviewInput
operator|.
name|robotComments
operator|=
name|Collections
operator|.
name|singletonMap
argument_list|(
name|robotCommentInput
operator|.
name|path
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|robotCommentInput
argument_list|)
argument_list|)
expr_stmt|;
name|reviewInput
operator|.
name|message
operator|=
literal|"robot comment test"
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|targetChangeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
block|}
DECL|method|getRobotComments ()
specifier|private
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
name|getRobotComments
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|robotCommentsAsList
argument_list|()
return|;
block|}
DECL|method|assertRobotComment (RobotCommentInfo c, RobotCommentInput expected)
specifier|private
name|void
name|assertRobotComment
parameter_list|(
name|RobotCommentInfo
name|c
parameter_list|,
name|RobotCommentInput
name|expected
parameter_list|)
block|{
name|assertRobotComment
argument_list|(
name|c
argument_list|,
name|expected
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRobotComment (RobotCommentInfo c, RobotCommentInput expected, boolean expectPath)
specifier|private
name|void
name|assertRobotComment
parameter_list|(
name|RobotCommentInfo
name|c
parameter_list|,
name|RobotCommentInput
name|expected
parameter_list|,
name|boolean
name|expectPath
parameter_list|)
block|{
name|assertThat
argument_list|(
name|c
operator|.
name|robotId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|robotId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|robotRunId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|robotRunId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|url
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|url
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|properties
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|properties
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|line
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|line
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|message
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|author
operator|.
name|email
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|admin
operator|.
name|email
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectPath
condition|)
block|{
name|assertThat
argument_list|(
name|c
operator|.
name|path
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|c
operator|.
name|path
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

