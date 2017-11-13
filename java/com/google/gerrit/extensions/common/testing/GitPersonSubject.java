begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.common.testing
package|package
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
name|testing
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
name|assertAbout
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
name|truth
operator|.
name|ComparableSubject
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
name|truth
operator|.
name|FailureMetadata
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
name|truth
operator|.
name|IntegerSubject
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
name|truth
operator|.
name|StringSubject
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
name|truth
operator|.
name|Subject
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
name|truth
operator|.
name|Truth
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
name|GitPerson
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_class
DECL|class|GitPersonSubject
specifier|public
class|class
name|GitPersonSubject
extends|extends
name|Subject
argument_list|<
name|GitPersonSubject
argument_list|,
name|GitPerson
argument_list|>
block|{
DECL|method|assertThat (GitPerson gitPerson)
specifier|public
specifier|static
name|GitPersonSubject
name|assertThat
parameter_list|(
name|GitPerson
name|gitPerson
parameter_list|)
block|{
return|return
name|assertAbout
argument_list|(
name|GitPersonSubject
operator|::
operator|new
argument_list|)
operator|.
name|that
argument_list|(
name|gitPerson
argument_list|)
return|;
block|}
DECL|method|GitPersonSubject (FailureMetadata failureMetadata, GitPerson gitPerson)
specifier|private
name|GitPersonSubject
parameter_list|(
name|FailureMetadata
name|failureMetadata
parameter_list|,
name|GitPerson
name|gitPerson
parameter_list|)
block|{
name|super
argument_list|(
name|failureMetadata
argument_list|,
name|gitPerson
argument_list|)
expr_stmt|;
block|}
DECL|method|name ()
specifier|public
name|StringSubject
name|name
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|GitPerson
name|gitPerson
init|=
name|actual
argument_list|()
decl_stmt|;
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|gitPerson
operator|.
name|name
argument_list|)
operator|.
name|named
argument_list|(
literal|"name"
argument_list|)
return|;
block|}
DECL|method|email ()
specifier|public
name|StringSubject
name|email
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|GitPerson
name|gitPerson
init|=
name|actual
argument_list|()
decl_stmt|;
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|gitPerson
operator|.
name|email
argument_list|)
operator|.
name|named
argument_list|(
literal|"email"
argument_list|)
return|;
block|}
DECL|method|date ()
specifier|public
name|ComparableSubject
argument_list|<
name|?
argument_list|,
name|Timestamp
argument_list|>
name|date
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|GitPerson
name|gitPerson
init|=
name|actual
argument_list|()
decl_stmt|;
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|gitPerson
operator|.
name|date
argument_list|)
operator|.
name|named
argument_list|(
literal|"date"
argument_list|)
return|;
block|}
DECL|method|tz ()
specifier|public
name|IntegerSubject
name|tz
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|GitPerson
name|gitPerson
init|=
name|actual
argument_list|()
decl_stmt|;
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|gitPerson
operator|.
name|tz
argument_list|)
operator|.
name|named
argument_list|(
literal|"tz"
argument_list|)
return|;
block|}
DECL|method|hasSameDateAs (GitPerson other)
specifier|public
name|void
name|hasSameDateAs
parameter_list|(
name|GitPerson
name|other
parameter_list|)
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|other
argument_list|)
operator|.
name|named
argument_list|(
literal|"other"
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|date
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|other
operator|.
name|date
argument_list|)
expr_stmt|;
name|tz
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|other
operator|.
name|tz
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

