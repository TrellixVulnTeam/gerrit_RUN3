begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.util
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|util
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|base
operator|.
name|Function
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
name|base
operator|.
name|Predicate
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
name|ImmutableList
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
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|primitives
operator|.
name|Chars
import|;
end_import

begin_import
import|import
name|dk
operator|.
name|brics
operator|.
name|automaton
operator|.
name|Automaton
import|;
end_import

begin_import
import|import
name|dk
operator|.
name|brics
operator|.
name|automaton
operator|.
name|RegExp
import|;
end_import

begin_import
import|import
name|dk
operator|.
name|brics
operator|.
name|automaton
operator|.
name|RunAutomaton
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
comment|/** Helper to search sorted lists for elements matching a regex. */
end_comment

begin_class
DECL|class|RegexListSearcher
specifier|public
specifier|abstract
class|class
name|RegexListSearcher
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Function
argument_list|<
name|T
argument_list|,
name|String
argument_list|>
block|{
DECL|method|ofStrings (String re)
specifier|public
specifier|static
name|RegexListSearcher
argument_list|<
name|String
argument_list|>
name|ofStrings
parameter_list|(
name|String
name|re
parameter_list|)
block|{
return|return
operator|new
name|RegexListSearcher
argument_list|<
name|String
argument_list|>
argument_list|(
name|re
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|in
parameter_list|)
block|{
return|return
name|in
return|;
block|}
block|}
return|;
block|}
DECL|field|pattern
specifier|private
specifier|final
name|RunAutomaton
name|pattern
decl_stmt|;
DECL|field|prefixBegin
specifier|private
specifier|final
name|String
name|prefixBegin
decl_stmt|;
DECL|field|prefixEnd
specifier|private
specifier|final
name|String
name|prefixEnd
decl_stmt|;
DECL|field|prefixLen
specifier|private
specifier|final
name|int
name|prefixLen
decl_stmt|;
DECL|field|prefixOnly
specifier|private
specifier|final
name|boolean
name|prefixOnly
decl_stmt|;
DECL|method|RegexListSearcher (String re)
specifier|public
name|RegexListSearcher
parameter_list|(
name|String
name|re
parameter_list|)
block|{
if|if
condition|(
name|re
operator|.
name|startsWith
argument_list|(
literal|"^"
argument_list|)
condition|)
block|{
name|re
operator|=
name|re
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|re
operator|.
name|endsWith
argument_list|(
literal|"$"
argument_list|)
operator|&&
operator|!
name|re
operator|.
name|endsWith
argument_list|(
literal|"\\$"
argument_list|)
condition|)
block|{
name|re
operator|=
name|re
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|re
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|Automaton
name|automaton
init|=
operator|new
name|RegExp
argument_list|(
name|re
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|prefixBegin
operator|=
name|automaton
operator|.
name|getCommonPrefix
argument_list|()
expr_stmt|;
name|prefixLen
operator|=
name|prefixBegin
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
literal|0
operator|<
name|prefixLen
condition|)
block|{
name|char
name|max
init|=
name|Chars
operator|.
name|checkedCast
argument_list|(
name|prefixBegin
operator|.
name|charAt
argument_list|(
name|prefixLen
operator|-
literal|1
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|prefixEnd
operator|=
name|prefixBegin
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|prefixLen
operator|-
literal|1
argument_list|)
operator|+
name|max
expr_stmt|;
name|prefixOnly
operator|=
name|re
operator|.
name|equals
argument_list|(
name|prefixBegin
operator|+
literal|".*"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prefixEnd
operator|=
literal|""
expr_stmt|;
name|prefixOnly
operator|=
literal|false
expr_stmt|;
block|}
name|pattern
operator|=
name|prefixOnly
condition|?
literal|null
else|:
operator|new
name|RunAutomaton
argument_list|(
name|automaton
argument_list|)
expr_stmt|;
block|}
DECL|method|search (List<T> list)
specifier|public
name|Iterable
argument_list|<
name|T
argument_list|>
name|search
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|int
name|begin
decl_stmt|;
name|int
name|end
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|prefixLen
condition|)
block|{
comment|// Assumes many consecutive elements may have the same prefix, so the cost
comment|// of two binary searches is less than iterating to find the endpoints.
name|begin
operator|=
name|find
argument_list|(
name|list
argument_list|,
name|prefixBegin
argument_list|)
expr_stmt|;
name|end
operator|=
name|find
argument_list|(
name|list
argument_list|,
name|prefixEnd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|begin
operator|=
literal|0
expr_stmt|;
name|end
operator|=
name|list
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|prefixOnly
condition|)
block|{
return|return
name|begin
operator|<
name|end
condition|?
name|list
operator|.
name|subList
argument_list|(
name|begin
argument_list|,
name|end
argument_list|)
else|:
name|ImmutableList
operator|.
expr|<
name|T
operator|>
name|of
argument_list|()
return|;
block|}
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|list
operator|.
name|subList
argument_list|(
name|begin
argument_list|,
name|end
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|T
name|in
parameter_list|)
block|{
return|return
name|pattern
operator|.
name|run
argument_list|(
name|RegexListSearcher
operator|.
name|this
operator|.
name|apply
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|hasMatch (List<T> list)
specifier|public
name|boolean
name|hasMatch
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|!
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|search
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
DECL|method|find (List<T> list, String p)
specifier|private
name|int
name|find
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|String
name|p
parameter_list|)
block|{
name|int
name|r
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|list
argument_list|,
name|this
argument_list|)
argument_list|,
name|p
argument_list|)
decl_stmt|;
return|return
name|r
operator|<
literal|0
condition|?
operator|-
operator|(
name|r
operator|+
literal|1
operator|)
else|:
name|r
return|;
block|}
block|}
end_class

end_unit

