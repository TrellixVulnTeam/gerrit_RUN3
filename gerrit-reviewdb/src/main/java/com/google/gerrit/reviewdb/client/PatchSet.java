begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Column
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|IntKey
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
name|Collections
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
comment|/** A single revision of a {@link Change}. */
end_comment

begin_class
DECL|class|PatchSet
specifier|public
specifier|final
class|class
name|PatchSet
block|{
comment|/** Is the reference name a change reference? */
DECL|method|isChangeRef (String name)
specifier|public
specifier|static
name|boolean
name|isChangeRef
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Id
operator|.
name|fromRef
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Is the reference name a change reference?    *    * @deprecated use isChangeRef instead.    **/
annotation|@
name|Deprecated
DECL|method|isRef (String name)
specifier|public
specifier|static
name|boolean
name|isRef
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|isChangeRef
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|joinGroups (List<String> groups)
specifier|static
name|String
name|joinGroups
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groups
parameter_list|)
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"groups may not be null"
argument_list|)
throw|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|g
range|:
name|groups
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|g
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|splitGroups (String joinedGroups)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|splitGroups
parameter_list|(
name|String
name|joinedGroups
parameter_list|)
block|{
if|if
condition|(
name|joinedGroups
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"groups may not be null"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|idx
init|=
name|joinedGroups
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
name|joinedGroups
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|joinedGroups
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|groups
operator|.
name|add
argument_list|(
name|joinedGroups
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|=
name|idx
operator|+
literal|1
expr_stmt|;
block|}
return|return
name|groups
return|;
block|}
DECL|class|Id
specifier|public
specifier|static
class|class
name|Id
extends|extends
name|IntKey
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|field|changeId
specifier|public
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|patchSetId
specifier|public
name|int
name|patchSetId
decl_stmt|;
DECL|method|Id ()
specifier|public
name|Id
parameter_list|()
block|{
name|changeId
operator|=
operator|new
name|Change
operator|.
name|Id
argument_list|()
expr_stmt|;
block|}
DECL|method|Id (final Change.Id change, final int id)
specifier|public
name|Id
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|change
parameter_list|,
specifier|final
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|changeId
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|patchSetId
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|Change
operator|.
name|Id
name|getParentKey
parameter_list|()
block|{
return|return
name|changeId
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|int
name|get
parameter_list|()
block|{
return|return
name|patchSetId
return|;
block|}
annotation|@
name|Override
DECL|method|set (int newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|int
name|newValue
parameter_list|)
block|{
name|patchSetId
operator|=
name|newValue
expr_stmt|;
block|}
DECL|method|toRefName ()
specifier|public
name|String
name|toRefName
parameter_list|()
block|{
return|return
name|changeId
operator|.
name|refPrefixBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Parse a PatchSet.Id out of a string representation. */
DECL|method|parse (final String str)
specifier|public
specifier|static
name|Id
name|parse
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
specifier|final
name|Id
name|r
init|=
operator|new
name|Id
argument_list|()
decl_stmt|;
name|r
operator|.
name|fromString
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
comment|/** Parse a PatchSet.Id from a {@link PatchSet#getRefName()} result. */
DECL|method|fromRef (String ref)
specifier|public
specifier|static
name|Id
name|fromRef
parameter_list|(
name|String
name|ref
parameter_list|)
block|{
name|int
name|cs
init|=
name|Change
operator|.
name|Id
operator|.
name|startIndex
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|ce
init|=
name|Change
operator|.
name|Id
operator|.
name|nextNonDigit
argument_list|(
name|ref
argument_list|,
name|cs
argument_list|)
decl_stmt|;
name|int
name|patchSetId
init|=
name|fromRef
argument_list|(
name|ref
argument_list|,
name|ce
argument_list|)
decl_stmt|;
if|if
condition|(
name|patchSetId
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|changeId
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|ref
operator|.
name|substring
argument_list|(
name|cs
argument_list|,
name|ce
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|patchSetId
argument_list|)
return|;
block|}
DECL|method|fromRef (String ref, int changeIdEnd)
specifier|static
name|int
name|fromRef
parameter_list|(
name|String
name|ref
parameter_list|,
name|int
name|changeIdEnd
parameter_list|)
block|{
comment|// Patch set ID.
name|int
name|ps
init|=
name|changeIdEnd
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|ps
operator|>=
name|ref
operator|.
name|length
argument_list|()
operator|||
name|ref
operator|.
name|charAt
argument_list|(
name|ps
argument_list|)
operator|==
literal|'0'
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
for|for
control|(
name|int
name|i
init|=
name|ps
init|;
name|i
operator|<
name|ref
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ref
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|<
literal|'0'
operator|||
name|ref
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|>
literal|'9'
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|ref
operator|.
name|substring
argument_list|(
name|ps
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|toId
argument_list|(
name|patchSetId
argument_list|)
return|;
block|}
DECL|method|toId (int number)
specifier|public
specifier|static
name|String
name|toId
parameter_list|(
name|int
name|number
parameter_list|)
block|{
return|return
name|number
operator|==
literal|0
condition|?
literal|"edit"
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|number
argument_list|)
return|;
block|}
block|}
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|,
name|name
operator|=
name|Column
operator|.
name|NONE
argument_list|)
DECL|field|id
specifier|protected
name|Id
name|id
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|revision
specifier|protected
name|RevId
name|revision
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|,
name|name
operator|=
literal|"uploader_account_id"
argument_list|)
DECL|field|uploader
specifier|protected
name|Account
operator|.
name|Id
name|uploader
decl_stmt|;
comment|/** When this patch set was first introduced onto the change. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|4
argument_list|)
DECL|field|createdOn
specifier|protected
name|Timestamp
name|createdOn
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|5
argument_list|)
DECL|field|draft
specifier|protected
name|boolean
name|draft
decl_stmt|;
comment|/**    * Opaque group identifier, usually assigned during creation.    *<p>    * This field is actually a comma-separated list of values, as in rare cases    * involving merge commits a patch set may belong to multiple groups.    *<p>    * Changes on the same branch having patch sets with intersecting groups are    * considered related, as in the "Related Changes" tab.    */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|6
argument_list|,
name|notNull
operator|=
literal|false
argument_list|,
name|length
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
DECL|field|groups
specifier|protected
name|String
name|groups
decl_stmt|;
comment|//DELETED id = 7 (pushCertficate)
comment|/** Certificate sent with a push that created this patch set. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|8
argument_list|,
name|notNull
operator|=
literal|false
argument_list|,
name|length
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
DECL|field|pushCertificate
specifier|protected
name|String
name|pushCertificate
decl_stmt|;
comment|/**    * Optional user-supplied description for this patch set.    *<p>    * When this field is null, the description was never set on the patch set.    * When this field is an empty string, the description was set and later    * cleared.    */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|9
argument_list|,
name|notNull
operator|=
literal|false
argument_list|,
name|length
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
DECL|field|description
specifier|protected
name|String
name|description
decl_stmt|;
DECL|method|PatchSet ()
specifier|protected
name|PatchSet
parameter_list|()
block|{   }
DECL|method|PatchSet (final PatchSet.Id k)
specifier|public
name|PatchSet
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|k
parameter_list|)
block|{
name|id
operator|=
name|k
expr_stmt|;
block|}
DECL|method|PatchSet (PatchSet src)
specifier|public
name|PatchSet
parameter_list|(
name|PatchSet
name|src
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|src
operator|.
name|id
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|src
operator|.
name|revision
expr_stmt|;
name|this
operator|.
name|uploader
operator|=
name|src
operator|.
name|uploader
expr_stmt|;
name|this
operator|.
name|createdOn
operator|=
name|src
operator|.
name|createdOn
expr_stmt|;
name|this
operator|.
name|draft
operator|=
name|src
operator|.
name|draft
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|src
operator|.
name|groups
expr_stmt|;
name|this
operator|.
name|pushCertificate
operator|=
name|src
operator|.
name|pushCertificate
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|src
operator|.
name|description
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|PatchSet
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getPatchSetId ()
specifier|public
name|int
name|getPatchSetId
parameter_list|()
block|{
return|return
name|id
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getRevision ()
specifier|public
name|RevId
name|getRevision
parameter_list|()
block|{
return|return
name|revision
return|;
block|}
DECL|method|setRevision (final RevId i)
specifier|public
name|void
name|setRevision
parameter_list|(
specifier|final
name|RevId
name|i
parameter_list|)
block|{
name|revision
operator|=
name|i
expr_stmt|;
block|}
DECL|method|getUploader ()
specifier|public
name|Account
operator|.
name|Id
name|getUploader
parameter_list|()
block|{
return|return
name|uploader
return|;
block|}
DECL|method|setUploader (final Account.Id who)
specifier|public
name|void
name|setUploader
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|who
parameter_list|)
block|{
name|uploader
operator|=
name|who
expr_stmt|;
block|}
DECL|method|getCreatedOn ()
specifier|public
name|Timestamp
name|getCreatedOn
parameter_list|()
block|{
return|return
name|createdOn
return|;
block|}
DECL|method|setCreatedOn (final Timestamp ts)
specifier|public
name|void
name|setCreatedOn
parameter_list|(
specifier|final
name|Timestamp
name|ts
parameter_list|)
block|{
name|createdOn
operator|=
name|ts
expr_stmt|;
block|}
DECL|method|isDraft ()
specifier|public
name|boolean
name|isDraft
parameter_list|()
block|{
return|return
name|draft
return|;
block|}
DECL|method|setDraft (boolean draftStatus)
specifier|public
name|void
name|setDraft
parameter_list|(
name|boolean
name|draftStatus
parameter_list|)
block|{
name|draft
operator|=
name|draftStatus
expr_stmt|;
block|}
DECL|method|getGroups ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|()
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
name|splitGroups
argument_list|(
name|groups
argument_list|)
return|;
block|}
DECL|method|setGroups (List<String> groups)
specifier|public
name|void
name|setGroups
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groups
parameter_list|)
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
name|groups
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|groups
operator|=
name|joinGroups
argument_list|(
name|groups
argument_list|)
expr_stmt|;
block|}
DECL|method|getRefName ()
specifier|public
name|String
name|getRefName
parameter_list|()
block|{
return|return
name|id
operator|.
name|toRefName
argument_list|()
return|;
block|}
DECL|method|getPushCertificate ()
specifier|public
name|String
name|getPushCertificate
parameter_list|()
block|{
return|return
name|pushCertificate
return|;
block|}
DECL|method|setPushCertificate (String cert)
specifier|public
name|void
name|setPushCertificate
parameter_list|(
name|String
name|cert
parameter_list|)
block|{
name|pushCertificate
operator|=
name|cert
expr_stmt|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|setDescription (String description)
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"[PatchSet "
operator|+
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

