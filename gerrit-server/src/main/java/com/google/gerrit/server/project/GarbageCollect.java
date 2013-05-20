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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|data
operator|.
name|GarbageCollectionResult
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
name|data
operator|.
name|GlobalCapability
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
name|annotations
operator|.
name|RequiresCapability
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
name|BinaryResult
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
name|RestModifyView
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
name|git
operator|.
name|GarbageCollection
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
name|project
operator|.
name|GarbageCollect
operator|.
name|Input
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|RUN_GC
argument_list|)
DECL|class|GarbageCollect
specifier|public
class|class
name|GarbageCollect
implements|implements
name|RestModifyView
argument_list|<
name|ProjectResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{   }
DECL|field|garbageCollectionFactory
specifier|private
name|GarbageCollection
operator|.
name|Factory
name|garbageCollectionFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|GarbageCollect (GarbageCollection.Factory garbageCollectionFactory)
name|GarbageCollect
parameter_list|(
name|GarbageCollection
operator|.
name|Factory
name|garbageCollectionFactory
parameter_list|)
block|{
name|this
operator|.
name|garbageCollectionFactory
operator|=
name|garbageCollectionFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (final ProjectResource rsrc, Input input)
specifier|public
name|BinaryResult
name|apply
parameter_list|(
specifier|final
name|ProjectResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
block|{
return|return
operator|new
name|BinaryResult
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|println
parameter_list|()
block|{
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|GarbageCollectionResult
name|result
init|=
name|garbageCollectionFactory
operator|.
name|create
argument_list|()
operator|.
name|run
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
argument_list|,
name|writer
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|hasErrors
argument_list|()
condition|)
block|{
for|for
control|(
name|GarbageCollectionResult
operator|.
name|Error
name|e
range|:
name|result
operator|.
name|getErrors
argument_list|()
control|)
block|{
name|String
name|msg
decl_stmt|;
switch|switch
condition|(
name|e
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|REPOSITORY_NOT_FOUND
case|:
name|msg
operator|=
literal|"error: project \""
operator|+
name|e
operator|.
name|getProjectName
argument_list|()
operator|+
literal|"\" not found"
expr_stmt|;
break|break;
case|case
name|GC_ALREADY_SCHEDULED
case|:
name|msg
operator|=
literal|"error: garbage collection for project \""
operator|+
name|e
operator|.
name|getProjectName
argument_list|()
operator|+
literal|"\" was already scheduled"
expr_stmt|;
break|break;
case|case
name|GC_FAILED
case|:
name|msg
operator|=
literal|"error: garbage collection for project \""
operator|+
name|e
operator|.
name|getProjectName
argument_list|()
operator|+
literal|"\" failed"
expr_stmt|;
break|break;
default|default:
name|msg
operator|=
literal|"error: garbage collection for project \""
operator|+
name|e
operator|.
name|getProjectName
argument_list|()
operator|+
literal|"\" failed: "
operator|+
name|e
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|setCharacterEncoding
argument_list|(
name|Charsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|disableGzip
argument_list|()
return|;
block|}
block|}
end_class

end_unit

