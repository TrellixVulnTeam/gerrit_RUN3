begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2008, Shawn O. Pearce<spearce@spearce.org>  *  * (Taken from JGit org.eclipse.jgit.pgm.opt.CmdLineParser.)  *  * All rights reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions are met:  *  * - Redistributions of source code must retain the above copyright notice, this  * list of conditions and the following disclaimer.  *  * - Redistributions in binary form must reproduce the above copyright notice,  * this list of conditions and the following disclaimer in the documentation  * and/or other materials provided with the distribution.  *  * - Neither the name of the Git Development Community nor the names of its  * contributors may be used to endorse or promote products derived from this  * software without specific prior written permission.  *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE  * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE  * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR  * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF  * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS  * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  * POSSIBILITY OF SUCH DAMAGE.  */
end_comment

begin_package
DECL|package|com.google.gerrit.util.cli
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|util
operator|.
name|cli
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
name|Strings
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
name|LinkedHashMultimap
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
name|collect
operator|.
name|Maps
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
name|Multimap
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
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|CmdLineException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|IllegalAnnotationError
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|NamedOptionDef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|OptionDef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|spi
operator|.
name|BooleanOptionHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|spi
operator|.
name|EnumOptionHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|spi
operator|.
name|OptionHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|spi
operator|.
name|Setter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|spi
operator|.
name|FieldSetter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Annotation
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
name|AnnotatedElement
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ResourceBundle
import|;
end_import

begin_comment
comment|/**  * Extended command line parser which handles --foo=value arguments.  *<p>  * The args4j package does not natively handle --foo=value and instead prefers  * to see --foo value on the command line. Many users are used to the GNU style  * --foo=value long option, so we convert from the GNU style format to the  * args4j style format prior to invoking args4j for parsing.  */
end_comment

begin_class
DECL|class|CmdLineParser
specifier|public
class|class
name|CmdLineParser
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Object bean)
name|CmdLineParser
name|create
parameter_list|(
name|Object
name|bean
parameter_list|)
function_decl|;
block|}
DECL|field|handlers
specifier|private
specifier|final
name|OptionHandlers
name|handlers
decl_stmt|;
DECL|field|parser
specifier|private
specifier|final
name|MyParser
name|parser
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|options
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|OptionHandler
argument_list|>
name|options
decl_stmt|;
comment|/**    * Creates a new command line owner that parses arguments/options and set them    * into the given object.    *    * @param bean instance of a class annotated by    *        {@link org.kohsuke.args4j.Option} and    *        {@link org.kohsuke.args4j.Argument}. this object will receive    *        values.    *    * @throws IllegalAnnotationError if the option bean class is using args4j    *         annotations incorrectly.    */
annotation|@
name|Inject
DECL|method|CmdLineParser (OptionHandlers handlers, @Assisted final Object bean)
specifier|public
name|CmdLineParser
parameter_list|(
name|OptionHandlers
name|handlers
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Object
name|bean
parameter_list|)
throws|throws
name|IllegalAnnotationError
block|{
name|this
operator|.
name|handlers
operator|=
name|handlers
expr_stmt|;
name|this
operator|.
name|parser
operator|=
operator|new
name|MyParser
argument_list|(
name|bean
argument_list|)
expr_stmt|;
block|}
DECL|method|addArgument (Setter<?> setter, Argument a)
specifier|public
name|void
name|addArgument
parameter_list|(
name|Setter
argument_list|<
name|?
argument_list|>
name|setter
parameter_list|,
name|Argument
name|a
parameter_list|)
block|{
name|parser
operator|.
name|addArgument
argument_list|(
name|setter
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
DECL|method|addOption (Setter<?> setter, Option o)
specifier|public
name|void
name|addOption
parameter_list|(
name|Setter
argument_list|<
name|?
argument_list|>
name|setter
parameter_list|,
name|Option
name|o
parameter_list|)
block|{
name|parser
operator|.
name|addOption
argument_list|(
name|setter
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|printSingleLineUsage (Writer w, ResourceBundle rb)
specifier|public
name|void
name|printSingleLineUsage
parameter_list|(
name|Writer
name|w
parameter_list|,
name|ResourceBundle
name|rb
parameter_list|)
block|{
name|parser
operator|.
name|printSingleLineUsage
argument_list|(
name|w
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
DECL|method|printUsage (Writer out, ResourceBundle rb)
specifier|public
name|void
name|printUsage
parameter_list|(
name|Writer
name|out
parameter_list|,
name|ResourceBundle
name|rb
parameter_list|)
block|{
name|parser
operator|.
name|printUsage
argument_list|(
name|out
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
DECL|method|printDetailedUsage (String name, StringWriter out)
specifier|public
name|void
name|printDetailedUsage
parameter_list|(
name|String
name|name
parameter_list|,
name|StringWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|printSingleLineUsage
argument_list|(
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
DECL|method|printQueryStringUsage (String name, StringWriter out)
specifier|public
name|void
name|printQueryStringUsage
parameter_list|(
name|String
name|name
parameter_list|,
name|StringWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|char
name|next
init|=
literal|'?'
decl_stmt|;
name|List
argument_list|<
name|NamedOptionDef
argument_list|>
name|booleans
init|=
operator|new
name|ArrayList
argument_list|<
name|NamedOptionDef
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|OptionHandler
name|handler
range|:
name|parser
operator|.
name|options
control|)
block|{
if|if
condition|(
name|handler
operator|.
name|option
operator|instanceof
name|NamedOptionDef
condition|)
block|{
name|NamedOptionDef
name|n
init|=
operator|(
name|NamedOptionDef
operator|)
name|handler
operator|.
name|option
decl_stmt|;
if|if
condition|(
name|handler
operator|instanceof
name|BooleanOptionHandler
condition|)
block|{
name|booleans
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|n
operator|.
name|required
argument_list|()
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|next
operator|=
literal|'&'
expr_stmt|;
if|if
condition|(
name|n
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|n
operator|.
name|name
argument_list|()
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|n
operator|.
name|name
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
name|n
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|metaVar
argument_list|(
name|handler
argument_list|,
name|n
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|n
operator|.
name|required
argument_list|()
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|.
name|isMultiValued
argument_list|()
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|NamedOptionDef
name|n
range|:
name|booleans
control|)
block|{
if|if
condition|(
operator|!
name|n
operator|.
name|required
argument_list|()
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|next
operator|=
literal|'&'
expr_stmt|;
if|if
condition|(
name|n
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|n
operator|.
name|name
argument_list|()
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|n
operator|.
name|name
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
name|n
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|n
operator|.
name|required
argument_list|()
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|metaVar (OptionHandler<?> handler, NamedOptionDef n)
specifier|private
specifier|static
name|String
name|metaVar
parameter_list|(
name|OptionHandler
argument_list|<
name|?
argument_list|>
name|handler
parameter_list|,
name|NamedOptionDef
name|n
parameter_list|)
block|{
name|String
name|var
init|=
name|n
operator|.
name|metaVar
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|var
argument_list|)
condition|)
block|{
name|var
operator|=
name|handler
operator|.
name|getDefaultMetaVariable
argument_list|()
expr_stmt|;
if|if
condition|(
name|handler
operator|instanceof
name|EnumOptionHandler
condition|)
block|{
name|var
operator|=
name|var
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|var
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|replace
argument_list|(
literal|" "
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|var
return|;
block|}
DECL|method|wasHelpRequestedByOption ()
specifier|public
name|boolean
name|wasHelpRequestedByOption
parameter_list|()
block|{
return|return
name|parser
operator|.
name|help
operator|.
name|value
return|;
block|}
DECL|method|parseArgument (final String... args)
specifier|public
name|void
name|parseArgument
parameter_list|(
specifier|final
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|CmdLineException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|tmp
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|args
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|argi
init|=
literal|0
init|;
name|argi
operator|<
name|args
operator|.
name|length
condition|;
name|argi
operator|++
control|)
block|{
specifier|final
name|String
name|str
init|=
name|args
index|[
name|argi
index|]
decl_stmt|;
if|if
condition|(
name|str
operator|.
name|equals
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
while|while
condition|(
name|argi
operator|<
name|args
operator|.
name|length
condition|)
name|tmp
operator|.
name|add
argument_list|(
name|args
index|[
name|argi
operator|++
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|str
operator|.
name|startsWith
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
specifier|final
name|int
name|eq
init|=
name|str
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|eq
operator|>
literal|0
condition|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|eq
argument_list|)
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|eq
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|tmp
operator|.
name|add
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
name|parser
operator|.
name|parseArgument
argument_list|(
name|tmp
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|tmp
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parseOptionMap (Map<String, String[]> parameters)
specifier|public
name|void
name|parseOptionMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|CmdLineException
block|{
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|LinkedHashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|ent
range|:
name|parameters
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|val
range|:
name|ent
operator|.
name|getValue
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|parseOptionMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
DECL|method|parseOptionMap (Multimap<String, String> params)
specifier|public
name|void
name|parseOptionMap
parameter_list|(
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|CmdLineException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|tmp
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
operator|*
name|params
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|key
range|:
name|params
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|makeOption
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|isBoolean
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|boolean
name|on
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
control|)
block|{
name|on
operator|=
name|toBoolean
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|on
condition|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|value
range|:
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
control|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|parser
operator|.
name|parseArgument
argument_list|(
name|tmp
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|tmp
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isBoolean (String name)
specifier|public
name|boolean
name|isBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|findHandler
argument_list|(
name|makeOption
argument_list|(
name|name
argument_list|)
argument_list|)
operator|instanceof
name|BooleanOptionHandler
return|;
block|}
DECL|method|makeOption (String name)
specifier|private
name|String
name|makeOption
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|name
operator|=
literal|"-"
operator|+
name|name
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|"--"
operator|+
name|name
expr_stmt|;
block|}
block|}
return|return
name|name
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|findHandler (String name)
specifier|private
name|OptionHandler
name|findHandler
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|options
operator|==
literal|null
condition|)
block|{
name|options
operator|=
name|index
argument_list|(
name|parser
operator|.
name|options
argument_list|)
expr_stmt|;
block|}
return|return
name|options
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|index (List<OptionHandler> in)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|OptionHandler
argument_list|>
name|index
parameter_list|(
name|List
argument_list|<
name|OptionHandler
argument_list|>
name|in
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|OptionHandler
argument_list|>
name|m
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|OptionHandler
name|handler
range|:
name|in
control|)
block|{
if|if
condition|(
name|handler
operator|.
name|option
operator|instanceof
name|NamedOptionDef
condition|)
block|{
name|NamedOptionDef
name|def
init|=
operator|(
name|NamedOptionDef
operator|)
name|handler
operator|.
name|option
decl_stmt|;
if|if
condition|(
operator|!
name|def
operator|.
name|isArgument
argument_list|()
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
name|def
operator|.
name|name
argument_list|()
argument_list|,
name|handler
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|alias
range|:
name|def
operator|.
name|aliases
argument_list|()
control|)
block|{
name|m
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|m
return|;
block|}
DECL|method|toBoolean (String name, String value)
specifier|private
name|boolean
name|toBoolean
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|CmdLineException
block|{
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"t"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"yes"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"y"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"on"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"1"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
name|value
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
literal|"false"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"f"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"no"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"n"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"off"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"0"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
throw|throw
operator|new
name|CmdLineException
argument_list|(
name|parser
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"invalid boolean \"%s=%s\""
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
throw|;
block|}
DECL|class|MyParser
specifier|private
class|class
name|MyParser
extends|extends
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|CmdLineParser
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|options
specifier|private
name|List
argument_list|<
name|OptionHandler
argument_list|>
name|options
decl_stmt|;
DECL|field|help
specifier|private
name|HelpOption
name|help
decl_stmt|;
DECL|method|MyParser (final Object bean)
name|MyParser
parameter_list|(
specifier|final
name|Object
name|bean
parameter_list|)
block|{
name|super
argument_list|(
name|bean
argument_list|)
expr_stmt|;
name|ensureOptionsInitialized
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
annotation|@
name|Override
DECL|method|createOptionHandler (final OptionDef option, final Setter setter)
specifier|protected
name|OptionHandler
name|createOptionHandler
parameter_list|(
specifier|final
name|OptionDef
name|option
parameter_list|,
specifier|final
name|Setter
name|setter
parameter_list|)
block|{
if|if
condition|(
name|isHandlerSpecified
argument_list|(
name|option
argument_list|)
operator|||
name|isEnum
argument_list|(
name|setter
argument_list|)
operator|||
name|isPrimitive
argument_list|(
name|setter
argument_list|)
condition|)
block|{
return|return
name|add
argument_list|(
name|super
operator|.
name|createOptionHandler
argument_list|(
name|option
argument_list|,
name|setter
argument_list|)
argument_list|)
return|;
block|}
name|OptionHandlerFactory
argument_list|<
name|?
argument_list|>
name|factory
init|=
name|handlers
operator|.
name|get
argument_list|(
name|setter
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
return|return
name|factory
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|option
argument_list|,
name|setter
argument_list|)
return|;
block|}
return|return
name|add
argument_list|(
name|super
operator|.
name|createOptionHandler
argument_list|(
name|option
argument_list|,
name|setter
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|add (OptionHandler handler)
specifier|private
name|OptionHandler
name|add
parameter_list|(
name|OptionHandler
name|handler
parameter_list|)
block|{
name|ensureOptionsInitialized
argument_list|()
expr_stmt|;
name|options
operator|.
name|add
argument_list|(
name|handler
argument_list|)
expr_stmt|;
return|return
name|handler
return|;
block|}
DECL|method|ensureOptionsInitialized ()
specifier|private
name|void
name|ensureOptionsInitialized
parameter_list|()
block|{
if|if
condition|(
name|options
operator|==
literal|null
condition|)
block|{
name|help
operator|=
operator|new
name|HelpOption
argument_list|()
expr_stmt|;
name|options
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|addOption
argument_list|(
name|help
argument_list|,
name|help
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isHandlerSpecified (final OptionDef option)
specifier|private
name|boolean
name|isHandlerSpecified
parameter_list|(
specifier|final
name|OptionDef
name|option
parameter_list|)
block|{
return|return
name|option
operator|.
name|handler
argument_list|()
operator|!=
name|OptionHandler
operator|.
name|class
return|;
block|}
DECL|method|isEnum (Setter<T> setter)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|boolean
name|isEnum
parameter_list|(
name|Setter
argument_list|<
name|T
argument_list|>
name|setter
parameter_list|)
block|{
return|return
name|Enum
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|setter
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|isPrimitive (Setter<T> setter)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|boolean
name|isPrimitive
parameter_list|(
name|Setter
argument_list|<
name|T
argument_list|>
name|setter
parameter_list|)
block|{
return|return
name|setter
operator|.
name|getType
argument_list|()
operator|.
name|isPrimitive
argument_list|()
return|;
block|}
block|}
DECL|class|HelpOption
specifier|private
specifier|static
class|class
name|HelpOption
implements|implements
name|Option
implements|,
name|Setter
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|field|value
specifier|private
name|boolean
name|value
decl_stmt|;
annotation|@
name|Override
DECL|method|name ()
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"--help"
return|;
block|}
annotation|@
name|Override
DECL|method|aliases ()
specifier|public
name|String
index|[]
name|aliases
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"-h"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|depends ()
specifier|public
name|String
index|[]
name|depends
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{}
return|;
block|}
annotation|@
name|Override
DECL|method|hidden ()
specifier|public
name|boolean
name|hidden
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|usage ()
specifier|public
name|String
name|usage
parameter_list|()
block|{
return|return
literal|"display this help text"
return|;
block|}
annotation|@
name|Override
DECL|method|addValue (Boolean val)
specifier|public
name|void
name|addValue
parameter_list|(
name|Boolean
name|val
parameter_list|)
block|{
name|value
operator|=
name|val
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handler ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|OptionHandler
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|handler
parameter_list|()
block|{
return|return
name|BooleanOptionHandler
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|metaVar ()
specifier|public
name|String
name|metaVar
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
DECL|method|required ()
specifier|public
name|boolean
name|required
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|annotationType ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|()
block|{
return|return
name|Option
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|asFieldSetter ()
specifier|public
name|FieldSetter
name|asFieldSetter
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|asAnnotatedElement ()
specifier|public
name|AnnotatedElement
name|asAnnotatedElement
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|Class
argument_list|<
name|Boolean
argument_list|>
name|getType
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued ()
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

