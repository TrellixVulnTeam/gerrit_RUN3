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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * This class represents inline comments in NoteDb. This means it determines the JSON format for  * inline comments in the revision notes that NoteDb uses to persist inline comments.  *  *<p>Changing fields in this class changes the storage format of inline comments in NoteDb and may  * require a corresponding data migration (adding new optional fields is generally okay).  *  *<p>{@link PatchLineComment} also represents inline comments, but in ReviewDb. There are a few  * notable differences:  *  *<ul>  *<li>PatchLineComment knows the comment status (published or draft). For comments in NoteDb the  *       status is determined by the branch in which they are stored (published comments are stored  *       in the change meta ref; draft comments are store in refs/draft-comments branches in  *       All-Users). Hence Comment doesn't need to contain the status, but the status is implicitly  *       known by where the comments are read from.  *<li>PatchLineComment knows the change ID. For comments in NoteDb, the change ID is determined  *       by the branch in which they are stored (the ref name contains the change ID). Hence Comment  *       doesn't need to contain the change ID, but the change ID is implicitly known by where the  *       comments are read from.  *</ul>  *  *<p>For all utility classes and middle layer functionality using Comment over PatchLineComment is  * preferred, as PatchLineComment will go away together with ReviewDb. This means Comment should be  * used everywhere and only for storing inline comment in ReviewDb a conversion to PatchLineComment  * is done. Converting Comments to PatchLineComments and vice verse is done by  * CommentsUtil#toPatchLineComments(Change.Id, PatchLineComment.Status, Iterable) and  * CommentsUtil#toComments(String, Iterable).  */
end_comment

begin_class
DECL|class|Comment
specifier|public
class|class
name|Comment
block|{
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
block|{
DECL|field|uuid
specifier|public
name|String
name|uuid
decl_stmt|;
DECL|field|filename
specifier|public
name|String
name|filename
decl_stmt|;
DECL|field|patchSetId
specifier|public
name|int
name|patchSetId
decl_stmt|;
DECL|method|Key (Key k)
specifier|public
name|Key
parameter_list|(
name|Key
name|k
parameter_list|)
block|{
name|this
argument_list|(
name|k
operator|.
name|uuid
argument_list|,
name|k
operator|.
name|filename
argument_list|,
name|k
operator|.
name|patchSetId
argument_list|)
expr_stmt|;
block|}
DECL|method|Key (String uuid, String filename, int patchSetId)
specifier|public
name|Key
parameter_list|(
name|String
name|uuid
parameter_list|,
name|String
name|filename
parameter_list|,
name|int
name|patchSetId
parameter_list|)
block|{
name|this
operator|.
name|uuid
operator|=
name|uuid
expr_stmt|;
name|this
operator|.
name|filename
operator|=
name|filename
expr_stmt|;
name|this
operator|.
name|patchSetId
operator|=
name|patchSetId
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
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"Comment.Key{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"uuid="
argument_list|)
operator|.
name|append
argument_list|(
name|uuid
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"filename="
argument_list|)
operator|.
name|append
argument_list|(
name|filename
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"patchSetId="
argument_list|)
operator|.
name|append
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Key
condition|)
block|{
name|Key
name|k
init|=
operator|(
name|Key
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|uuid
argument_list|,
name|k
operator|.
name|uuid
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|filename
argument_list|,
name|k
operator|.
name|filename
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|patchSetId
argument_list|,
name|k
operator|.
name|patchSetId
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|uuid
argument_list|,
name|filename
argument_list|,
name|patchSetId
argument_list|)
return|;
block|}
block|}
DECL|class|Identity
specifier|public
specifier|static
class|class
name|Identity
block|{
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|method|Identity (Account.Id id)
specifier|public
name|Identity
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|Account
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Identity
condition|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|id
argument_list|,
operator|(
operator|(
name|Identity
operator|)
name|o
operator|)
operator|.
name|id
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|id
argument_list|)
return|;
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
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"Comment.Identity{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"id="
argument_list|)
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|Range
specifier|public
specifier|static
class|class
name|Range
implements|implements
name|Comparable
argument_list|<
name|Range
argument_list|>
block|{
DECL|field|RANGE_COMPARATOR
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Range
argument_list|>
name|RANGE_COMPARATOR
init|=
name|Comparator
operator|.
expr|<
name|Range
operator|>
name|comparingInt
argument_list|(
name|range
lambda|->
name|range
operator|.
name|startLine
argument_list|)
operator|.
name|thenComparingInt
argument_list|(
name|range
lambda|->
name|range
operator|.
name|startChar
argument_list|)
operator|.
name|thenComparingInt
argument_list|(
name|range
lambda|->
name|range
operator|.
name|endLine
argument_list|)
operator|.
name|thenComparingInt
argument_list|(
name|range
lambda|->
name|range
operator|.
name|endChar
argument_list|)
decl_stmt|;
DECL|field|startLine
specifier|public
name|int
name|startLine
decl_stmt|;
comment|// 1-based, inclusive
DECL|field|startChar
specifier|public
name|int
name|startChar
decl_stmt|;
comment|// 0-based, inclusive
DECL|field|endLine
specifier|public
name|int
name|endLine
decl_stmt|;
comment|// 1-based, exclusive
DECL|field|endChar
specifier|public
name|int
name|endChar
decl_stmt|;
comment|// 0-based, exclusive
DECL|method|Range (Range r)
specifier|public
name|Range
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
name|this
argument_list|(
name|r
operator|.
name|startLine
argument_list|,
name|r
operator|.
name|startChar
argument_list|,
name|r
operator|.
name|endLine
argument_list|,
name|r
operator|.
name|endChar
argument_list|)
expr_stmt|;
block|}
DECL|method|Range (com.google.gerrit.extensions.client.Comment.Range r)
specifier|public
name|Range
parameter_list|(
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
operator|.
name|Range
name|r
parameter_list|)
block|{
name|this
argument_list|(
name|r
operator|.
name|startLine
argument_list|,
name|r
operator|.
name|startCharacter
argument_list|,
name|r
operator|.
name|endLine
argument_list|,
name|r
operator|.
name|endCharacter
argument_list|)
expr_stmt|;
block|}
DECL|method|Range (int startLine, int startChar, int endLine, int endChar)
specifier|public
name|Range
parameter_list|(
name|int
name|startLine
parameter_list|,
name|int
name|startChar
parameter_list|,
name|int
name|endLine
parameter_list|,
name|int
name|endChar
parameter_list|)
block|{
name|this
operator|.
name|startLine
operator|=
name|startLine
expr_stmt|;
name|this
operator|.
name|startChar
operator|=
name|startChar
expr_stmt|;
name|this
operator|.
name|endLine
operator|=
name|endLine
expr_stmt|;
name|this
operator|.
name|endChar
operator|=
name|endChar
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Range
condition|)
block|{
name|Range
name|r
init|=
operator|(
name|Range
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|startLine
argument_list|,
name|r
operator|.
name|startLine
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|startChar
argument_list|,
name|r
operator|.
name|startChar
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|endLine
argument_list|,
name|r
operator|.
name|endLine
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|endChar
argument_list|,
name|r
operator|.
name|endChar
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|startLine
argument_list|,
name|startChar
argument_list|,
name|endLine
argument_list|,
name|endChar
argument_list|)
return|;
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
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"Comment.Range{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"startLine="
argument_list|)
operator|.
name|append
argument_list|(
name|startLine
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"startChar="
argument_list|)
operator|.
name|append
argument_list|(
name|startChar
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"endLine="
argument_list|)
operator|.
name|append
argument_list|(
name|endLine
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"endChar="
argument_list|)
operator|.
name|append
argument_list|(
name|endChar
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Range otherRange)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Range
name|otherRange
parameter_list|)
block|{
return|return
name|RANGE_COMPARATOR
operator|.
name|compare
argument_list|(
name|this
argument_list|,
name|otherRange
argument_list|)
return|;
block|}
block|}
DECL|field|key
specifier|public
name|Key
name|key
decl_stmt|;
DECL|field|lineNbr
specifier|public
name|int
name|lineNbr
decl_stmt|;
DECL|field|author
specifier|public
name|Identity
name|author
decl_stmt|;
DECL|field|realAuthor
specifier|protected
name|Identity
name|realAuthor
decl_stmt|;
DECL|field|writtenOn
specifier|public
name|Timestamp
name|writtenOn
decl_stmt|;
DECL|field|side
specifier|public
name|short
name|side
decl_stmt|;
DECL|field|message
specifier|public
name|String
name|message
decl_stmt|;
DECL|field|parentUuid
specifier|public
name|String
name|parentUuid
decl_stmt|;
DECL|field|range
specifier|public
name|Range
name|range
decl_stmt|;
DECL|field|tag
specifier|public
name|String
name|tag
decl_stmt|;
comment|// Hex commit SHA1 of the commit of the patchset to which this comment applies.
DECL|field|revId
specifier|public
name|String
name|revId
decl_stmt|;
DECL|field|serverId
specifier|public
name|String
name|serverId
decl_stmt|;
DECL|field|unresolved
specifier|public
name|boolean
name|unresolved
decl_stmt|;
comment|/**    * Whether the comment was parsed from a JSON representation (false) or the legacy custom notes    * format (true).    */
DECL|field|legacyFormat
specifier|public
specifier|transient
name|boolean
name|legacyFormat
decl_stmt|;
DECL|method|Comment (Comment c)
specifier|public
name|Comment
parameter_list|(
name|Comment
name|c
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Key
argument_list|(
name|c
operator|.
name|key
argument_list|)
argument_list|,
name|c
operator|.
name|author
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|Timestamp
argument_list|(
name|c
operator|.
name|writtenOn
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|,
name|c
operator|.
name|side
argument_list|,
name|c
operator|.
name|message
argument_list|,
name|c
operator|.
name|serverId
argument_list|,
name|c
operator|.
name|unresolved
argument_list|)
expr_stmt|;
name|this
operator|.
name|lineNbr
operator|=
name|c
operator|.
name|lineNbr
expr_stmt|;
name|this
operator|.
name|realAuthor
operator|=
name|c
operator|.
name|realAuthor
expr_stmt|;
name|this
operator|.
name|range
operator|=
name|c
operator|.
name|range
operator|!=
literal|null
condition|?
operator|new
name|Range
argument_list|(
name|c
operator|.
name|range
argument_list|)
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|tag
operator|=
name|c
operator|.
name|tag
expr_stmt|;
name|this
operator|.
name|revId
operator|=
name|c
operator|.
name|revId
expr_stmt|;
name|this
operator|.
name|unresolved
operator|=
name|c
operator|.
name|unresolved
expr_stmt|;
block|}
DECL|method|Comment ( Key key, Account.Id author, Timestamp writtenOn, short side, String message, String serverId, boolean unresolved)
specifier|public
name|Comment
parameter_list|(
name|Key
name|key
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|,
name|Timestamp
name|writtenOn
parameter_list|,
name|short
name|side
parameter_list|,
name|String
name|message
parameter_list|,
name|String
name|serverId
parameter_list|,
name|boolean
name|unresolved
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|author
operator|=
operator|new
name|Comment
operator|.
name|Identity
argument_list|(
name|author
argument_list|)
expr_stmt|;
name|this
operator|.
name|realAuthor
operator|=
name|this
operator|.
name|author
expr_stmt|;
name|this
operator|.
name|writtenOn
operator|=
name|writtenOn
expr_stmt|;
name|this
operator|.
name|side
operator|=
name|side
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|serverId
operator|=
name|serverId
expr_stmt|;
name|this
operator|.
name|unresolved
operator|=
name|unresolved
expr_stmt|;
block|}
DECL|method|setLineNbrAndRange ( Integer lineNbr, com.google.gerrit.extensions.client.Comment.Range range)
specifier|public
name|void
name|setLineNbrAndRange
parameter_list|(
name|Integer
name|lineNbr
parameter_list|,
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
operator|.
name|Range
name|range
parameter_list|)
block|{
name|this
operator|.
name|lineNbr
operator|=
name|lineNbr
operator|!=
literal|null
condition|?
name|lineNbr
else|:
name|range
operator|!=
literal|null
condition|?
name|range
operator|.
name|endLine
else|:
literal|0
expr_stmt|;
if|if
condition|(
name|range
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|range
operator|=
operator|new
name|Comment
operator|.
name|Range
argument_list|(
name|range
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setRange (CommentRange range)
specifier|public
name|void
name|setRange
parameter_list|(
name|CommentRange
name|range
parameter_list|)
block|{
name|this
operator|.
name|range
operator|=
name|range
operator|!=
literal|null
condition|?
name|range
operator|.
name|asCommentRange
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
DECL|method|setRevId (RevId revId)
specifier|public
name|void
name|setRevId
parameter_list|(
name|RevId
name|revId
parameter_list|)
block|{
name|this
operator|.
name|revId
operator|=
name|revId
operator|!=
literal|null
condition|?
name|revId
operator|.
name|get
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
DECL|method|setRealAuthor (Account.Id id)
specifier|public
name|void
name|setRealAuthor
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|realAuthor
operator|=
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|get
argument_list|()
operator|!=
name|author
operator|.
name|id
condition|?
operator|new
name|Comment
operator|.
name|Identity
argument_list|(
name|id
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
DECL|method|getRealAuthor ()
specifier|public
name|Identity
name|getRealAuthor
parameter_list|()
block|{
return|return
name|realAuthor
operator|!=
literal|null
condition|?
name|realAuthor
else|:
name|author
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Comment
condition|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|key
argument_list|,
operator|(
operator|(
name|Comment
operator|)
name|o
operator|)
operator|.
name|key
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|key
operator|.
name|hashCode
argument_list|()
return|;
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
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"Comment{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"key="
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"lineNbr="
argument_list|)
operator|.
name|append
argument_list|(
name|lineNbr
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"author="
argument_list|)
operator|.
name|append
argument_list|(
name|author
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"realAuthor="
argument_list|)
operator|.
name|append
argument_list|(
name|realAuthor
operator|!=
literal|null
condition|?
name|realAuthor
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
else|:
literal|""
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"writtenOn="
argument_list|)
operator|.
name|append
argument_list|(
name|writtenOn
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"side="
argument_list|)
operator|.
name|append
argument_list|(
name|side
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"message="
argument_list|)
operator|.
name|append
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|message
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"parentUuid="
argument_list|)
operator|.
name|append
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|parentUuid
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"range="
argument_list|)
operator|.
name|append
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|range
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"revId="
argument_list|)
operator|.
name|append
argument_list|(
name|revId
operator|!=
literal|null
condition|?
name|revId
else|:
literal|""
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"tag="
argument_list|)
operator|.
name|append
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|tag
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|"unresolved="
argument_list|)
operator|.
name|append
argument_list|(
name|unresolved
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

