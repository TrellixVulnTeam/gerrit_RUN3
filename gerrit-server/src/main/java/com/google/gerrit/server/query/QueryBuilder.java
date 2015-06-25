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
DECL|package|com.google.gerrit.server.query
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|query
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
name|query
operator|.
name|Predicate
operator|.
name|and
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
name|query
operator|.
name|Predicate
operator|.
name|not
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
name|query
operator|.
name|Predicate
operator|.
name|or
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
name|query
operator|.
name|QueryParser
operator|.
name|AND
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
name|query
operator|.
name|QueryParser
operator|.
name|DEFAULT_FIELD
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
name|query
operator|.
name|QueryParser
operator|.
name|EXACT_PHRASE
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
name|query
operator|.
name|QueryParser
operator|.
name|FIELD_NAME
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
name|query
operator|.
name|QueryParser
operator|.
name|NOT
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
name|query
operator|.
name|QueryParser
operator|.
name|OR
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
name|query
operator|.
name|QueryParser
operator|.
name|SINGLE_WORD
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|runtime
operator|.
name|tree
operator|.
name|Tree
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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

begin_comment
comment|/**  * Base class to support writing parsers for query languages.  *<p>  * Subclasses may document their supported query operators by declaring public  * methods that perform the query conversion into a {@link Predicate}. For  * example, to support "is:starred", "is:unread", and nothing else, a subclass  * may write:  *  *<pre>  *&#064;Operator  * public Predicate is(final String value) {  *   if (&quot;starred&quot;.equals(value)) {  *     return new StarredPredicate();  *   }  *   if (&quot;unread&quot;.equals(value)) {  *     return new UnreadPredicate();  *   }  *   throw new IllegalArgumentException();  * }  *</pre>  *<p>  * The available operator methods are discovered at runtime via reflection.  * Method names (after being converted to lowercase), correspond to operators in  * the query language, method string values correspond to the operator argument.  * Methods must be declared {@code public}, returning {@link Predicate},  * accepting one {@link String}, and annotated with the {@link Operator}  * annotation.  *<p>  * Subclasses may also declare a handler for values which appear without  * operator by overriding {@link #defaultField(String)}.  *  * @param<T> type of object the predicates can evaluate in memory.  */
end_comment

begin_class
DECL|class|QueryBuilder
specifier|public
specifier|abstract
class|class
name|QueryBuilder
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Defines the operators known by a QueryBuilder.    *    * This class is thread-safe and may be reused or cached.    *    * @param<T> type of object the predicates can evaluate in memory.    * @param<Q> type of the query builder subclass.    */
DECL|class|Definition
specifier|public
specifier|static
class|class
name|Definition
parameter_list|<
name|T
parameter_list|,
name|Q
extends|extends
name|QueryBuilder
parameter_list|<
name|T
parameter_list|>
parameter_list|>
block|{
DECL|field|opFactories
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OperatorFactory
argument_list|<
name|T
argument_list|,
name|Q
argument_list|>
argument_list|>
name|opFactories
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Definition (Class<Q> clazz)
specifier|public
name|Definition
parameter_list|(
name|Class
argument_list|<
name|Q
argument_list|>
name|clazz
parameter_list|)
block|{
comment|// Guess at the supported operators by scanning methods.
comment|//
name|Class
argument_list|<
name|?
argument_list|>
name|c
init|=
name|clazz
decl_stmt|;
while|while
condition|(
name|c
operator|!=
name|QueryBuilder
operator|.
name|class
condition|)
block|{
for|for
control|(
specifier|final
name|Method
name|method
range|:
name|c
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|method
operator|.
name|getAnnotation
argument_list|(
name|Operator
operator|.
name|class
argument_list|)
operator|!=
literal|null
operator|&&
name|Predicate
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
operator|&&
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
operator|==
name|String
operator|.
name|class
operator|&&
operator|(
name|method
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|ABSTRACT
operator|)
operator|==
literal|0
operator|&&
operator|(
name|method
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|PUBLIC
operator|)
operator|==
name|Modifier
operator|.
name|PUBLIC
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|method
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|opFactories
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|opFactories
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|ReflectionFactory
argument_list|<
name|T
argument_list|,
name|Q
argument_list|>
argument_list|(
name|name
argument_list|,
name|method
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|c
operator|=
name|c
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Locate a predicate in the predicate tree.    *    * @param p the predicate to find.    * @param clazz type of the predicate instance.    * @return the predicate, null if not found.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|find (Predicate<T> p, Class<P> clazz)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|P
extends|extends
name|Predicate
argument_list|<
name|T
argument_list|>
parameter_list|>
name|P
name|find
parameter_list|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|p
parameter_list|,
name|Class
argument_list|<
name|P
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isAssignableFrom
argument_list|(
name|p
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|(
name|P
operator|)
name|p
return|;
block|}
for|for
control|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|c
range|:
name|p
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|P
name|r
init|=
name|find
argument_list|(
name|c
argument_list|,
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
return|return
name|r
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Locate a predicate in the predicate tree.    *    * @param p the predicate to find.    * @param clazz type of the predicate instance.    * @param name name of the operator.    * @return the first instance of a predicate having the given type, as found    *     by a depth-first search.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|find (Predicate<T> p, Class<P> clazz, String name)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|P
extends|extends
name|OperatorPredicate
argument_list|<
name|T
argument_list|>
parameter_list|>
name|P
name|find
parameter_list|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|p
parameter_list|,
name|Class
argument_list|<
name|P
argument_list|>
name|clazz
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|OperatorPredicate
operator|&&
operator|(
operator|(
name|OperatorPredicate
argument_list|<
name|?
argument_list|>
operator|)
name|p
operator|)
operator|.
name|getOperator
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|clazz
operator|.
name|isAssignableFrom
argument_list|(
name|p
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|(
name|P
operator|)
name|p
return|;
block|}
for|for
control|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|c
range|:
name|p
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|P
name|r
init|=
name|find
argument_list|(
name|c
argument_list|,
name|clazz
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
return|return
name|r
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|field|builderDef
specifier|protected
specifier|final
name|Definition
argument_list|<
name|T
argument_list|,
name|?
extends|extends
name|QueryBuilder
argument_list|<
name|T
argument_list|>
argument_list|>
name|builderDef
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|opFactories
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OperatorFactory
argument_list|>
name|opFactories
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|QueryBuilder (Definition<T, ? extends QueryBuilder<T>> def)
specifier|protected
name|QueryBuilder
parameter_list|(
name|Definition
argument_list|<
name|T
argument_list|,
name|?
extends|extends
name|QueryBuilder
argument_list|<
name|T
argument_list|>
argument_list|>
name|def
parameter_list|)
block|{
name|builderDef
operator|=
name|def
expr_stmt|;
name|opFactories
operator|=
operator|(
name|Map
operator|)
name|def
operator|.
name|opFactories
expr_stmt|;
block|}
comment|/**    * Parse a user-supplied query string into a predicate.    *    * @param query the query string.    * @return predicate representing the user query.    * @throws QueryParseException the query string is invalid and cannot be    *         parsed by this parser. This may be due to a syntax error, may be    *         due to an operator not being supported, or due to an invalid value    *         being passed to a recognized operator.    */
DECL|method|parse (final String query)
specifier|public
name|Predicate
argument_list|<
name|T
argument_list|>
name|parse
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|QueryParseException
block|{
return|return
name|toPredicate
argument_list|(
name|QueryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Parse multiple user-supplied query strings into a list of predicates.    *    * @param queries the query strings.    * @return predicates representing the user query, in the same order as the    *         input.    * @throws QueryParseException one of the query strings is invalid and cannot    *         be parsed by this parser. This may be due to a syntax error, may be    *         due to an operator not being supported, or due to an invalid value    *         being passed to a recognized operator.    *    */
DECL|method|parse (final List<String> queries)
specifier|public
name|List
argument_list|<
name|Predicate
argument_list|<
name|T
argument_list|>
argument_list|>
name|parse
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|queries
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|List
argument_list|<
name|Predicate
argument_list|<
name|T
argument_list|>
argument_list|>
name|predicates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|queries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|query
range|:
name|queries
control|)
block|{
name|predicates
operator|.
name|add
argument_list|(
name|parse
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|predicates
return|;
block|}
DECL|method|toPredicate (final Tree r)
specifier|private
name|Predicate
argument_list|<
name|T
argument_list|>
name|toPredicate
parameter_list|(
specifier|final
name|Tree
name|r
parameter_list|)
throws|throws
name|QueryParseException
throws|,
name|IllegalArgumentException
block|{
switch|switch
condition|(
name|r
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|AND
case|:
return|return
name|and
argument_list|(
name|children
argument_list|(
name|r
argument_list|)
argument_list|)
return|;
case|case
name|OR
case|:
return|return
name|or
argument_list|(
name|children
argument_list|(
name|r
argument_list|)
argument_list|)
return|;
case|case
name|NOT
case|:
return|return
name|not
argument_list|(
name|toPredicate
argument_list|(
name|onlyChildOf
argument_list|(
name|r
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
name|DEFAULT_FIELD
case|:
return|return
name|defaultField
argument_list|(
name|onlyChildOf
argument_list|(
name|r
argument_list|)
argument_list|)
return|;
case|case
name|FIELD_NAME
case|:
return|return
name|operator
argument_list|(
name|r
operator|.
name|getText
argument_list|()
argument_list|,
name|onlyChildOf
argument_list|(
name|r
argument_list|)
argument_list|)
return|;
default|default:
throw|throw
name|error
argument_list|(
literal|"Unsupported operator: "
operator|+
name|r
argument_list|)
throw|;
block|}
block|}
DECL|method|operator (final String name, final Tree val)
specifier|private
name|Predicate
argument_list|<
name|T
argument_list|>
name|operator
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Tree
name|val
parameter_list|)
throws|throws
name|QueryParseException
block|{
switch|switch
condition|(
name|val
operator|.
name|getType
argument_list|()
condition|)
block|{
comment|// Expand multiple values, "foo:(a b c)", as though they were written
comment|// out with the longer form, "foo:a foo:b foo:c".
comment|//
case|case
name|AND
case|:
case|case
name|OR
case|:
block|{
name|List
argument_list|<
name|Predicate
argument_list|<
name|T
argument_list|>
argument_list|>
name|p
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|val
operator|.
name|getChildCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|val
operator|.
name|getChildCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Tree
name|c
init|=
name|val
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|getType
argument_list|()
operator|!=
name|DEFAULT_FIELD
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"Nested operator not expected: "
operator|+
name|c
argument_list|)
throw|;
block|}
name|p
operator|.
name|add
argument_list|(
name|operator
argument_list|(
name|name
argument_list|,
name|onlyChildOf
argument_list|(
name|c
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|val
operator|.
name|getType
argument_list|()
operator|==
name|AND
condition|?
name|and
argument_list|(
name|p
argument_list|)
else|:
name|or
argument_list|(
name|p
argument_list|)
return|;
block|}
case|case
name|SINGLE_WORD
case|:
case|case
name|EXACT_PHRASE
case|:
if|if
condition|(
name|val
operator|.
name|getChildCount
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"Expected no children under: "
operator|+
name|val
argument_list|)
throw|;
block|}
return|return
name|operator
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|getText
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
name|error
argument_list|(
literal|"Unsupported node in operator "
operator|+
name|name
operator|+
literal|": "
operator|+
name|val
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|operator (final String name, final String value)
specifier|private
name|Predicate
argument_list|<
name|T
argument_list|>
name|operator
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|QueryParseException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|OperatorFactory
name|f
init|=
name|opFactories
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"Unsupported operator "
operator|+
name|name
operator|+
literal|":"
operator|+
name|value
argument_list|)
throw|;
block|}
return|return
name|f
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|defaultField (final Tree r)
specifier|private
name|Predicate
argument_list|<
name|T
argument_list|>
name|defaultField
parameter_list|(
specifier|final
name|Tree
name|r
parameter_list|)
throws|throws
name|QueryParseException
block|{
switch|switch
condition|(
name|r
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|SINGLE_WORD
case|:
case|case
name|EXACT_PHRASE
case|:
if|if
condition|(
name|r
operator|.
name|getChildCount
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"Expected no children under: "
operator|+
name|r
argument_list|)
throw|;
block|}
return|return
name|defaultField
argument_list|(
name|r
operator|.
name|getText
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
name|error
argument_list|(
literal|"Unsupported node: "
operator|+
name|r
argument_list|)
throw|;
block|}
block|}
comment|/**    * Handle a value present outside of an operator.    *<p>    * This default implementation always throws an "Unsupported query: " message    * containing the input text. Subclasses may override this method to perform    * do-what-i-mean guesses based on the input string.    *    * @param value the value supplied by itself in the query.    * @return predicate representing this value.    * @throws QueryParseException the parser does not recognize this value.    */
DECL|method|defaultField (final String value)
specifier|protected
name|Predicate
argument_list|<
name|T
argument_list|>
name|defaultField
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|QueryParseException
block|{
throw|throw
name|error
argument_list|(
literal|"Unsupported query:"
operator|+
name|value
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|children (final Tree r)
specifier|private
name|Predicate
argument_list|<
name|T
argument_list|>
index|[]
name|children
parameter_list|(
specifier|final
name|Tree
name|r
parameter_list|)
throws|throws
name|QueryParseException
throws|,
name|IllegalArgumentException
block|{
specifier|final
name|Predicate
argument_list|<
name|T
argument_list|>
index|[]
name|p
init|=
operator|new
name|Predicate
index|[
name|r
operator|.
name|getChildCount
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|p
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|p
index|[
name|i
index|]
operator|=
name|toPredicate
argument_list|(
name|r
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
DECL|method|onlyChildOf (final Tree r)
specifier|private
name|Tree
name|onlyChildOf
parameter_list|(
specifier|final
name|Tree
name|r
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|r
operator|.
name|getChildCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"Expected exactly one child: "
operator|+
name|r
argument_list|)
throw|;
block|}
return|return
name|r
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|error (String msg)
specifier|protected
specifier|static
name|QueryParseException
name|error
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
return|return
operator|new
name|QueryParseException
argument_list|(
name|msg
argument_list|)
return|;
block|}
DECL|method|error (String msg, Throwable why)
specifier|protected
specifier|static
name|QueryParseException
name|error
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|why
parameter_list|)
block|{
return|return
operator|new
name|QueryParseException
argument_list|(
name|msg
argument_list|,
name|why
argument_list|)
return|;
block|}
comment|/** Converts a value string passed to an operator into a {@link Predicate}. */
DECL|interface|OperatorFactory
specifier|protected
interface|interface
name|OperatorFactory
parameter_list|<
name|T
parameter_list|,
name|Q
extends|extends
name|QueryBuilder
parameter_list|<
name|T
parameter_list|>
parameter_list|>
block|{
DECL|method|create (Q builder, String value)
name|Predicate
argument_list|<
name|T
argument_list|>
name|create
parameter_list|(
name|Q
name|builder
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|QueryParseException
function_decl|;
block|}
comment|/** Denotes a method which is a query operator. */
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
DECL|annotation|Operator
specifier|protected
annotation_defn|@interface
name|Operator
block|{   }
DECL|class|ReflectionFactory
specifier|private
specifier|static
class|class
name|ReflectionFactory
parameter_list|<
name|T
parameter_list|,
name|Q
extends|extends
name|QueryBuilder
parameter_list|<
name|T
parameter_list|>
parameter_list|>
implements|implements
name|OperatorFactory
argument_list|<
name|T
argument_list|,
name|Q
argument_list|>
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|method
specifier|private
specifier|final
name|Method
name|method
decl_stmt|;
DECL|method|ReflectionFactory (final String name, final Method method)
name|ReflectionFactory
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Method
name|method
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|create (Q builder, String value)
specifier|public
name|Predicate
argument_list|<
name|T
argument_list|>
name|create
parameter_list|(
name|Q
name|builder
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|QueryParseException
block|{
try|try
block|{
return|return
operator|(
name|Predicate
argument_list|<
name|T
argument_list|>
operator|)
name|method
operator|.
name|invoke
argument_list|(
name|builder
argument_list|,
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
name|error
argument_list|(
literal|"Error in operator "
operator|+
name|name
operator|+
literal|":"
operator|+
name|value
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|error
argument_list|(
literal|"Error in operator "
operator|+
name|name
operator|+
literal|":"
operator|+
name|value
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

