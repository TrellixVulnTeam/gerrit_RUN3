begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.restapi
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|restapi
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
name|httpd
operator|.
name|restapi
operator|.
name|RestApiServlet
operator|.
name|replyBinaryResult
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
name|httpd
operator|.
name|restapi
operator|.
name|RestApiServlet
operator|.
name|replyError
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Splitter
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
name|ImmutableSet
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
name|ListMultimap
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
name|registration
operator|.
name|DynamicMap
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
name|Url
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
name|DynamicOptions
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
name|util
operator|.
name|cli
operator|.
name|CmdLineParser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonArray
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonElement
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonPrimitive
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|server
operator|.
name|CacheHeaders
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
name|StringWriter
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
name|Iterator
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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

begin_class
DECL|class|ParameterParser
class|class
name|ParameterParser
block|{
DECL|field|RESERVED_KEYS
specifier|private
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|RESERVED_KEYS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"pp"
argument_list|,
literal|"prettyPrint"
argument_list|,
literal|"strict"
argument_list|,
literal|"callback"
argument_list|,
literal|"alt"
argument_list|,
literal|"fields"
argument_list|)
decl_stmt|;
DECL|field|parserFactory
specifier|private
specifier|final
name|CmdLineParser
operator|.
name|Factory
name|parserFactory
decl_stmt|;
DECL|field|dynamicBeans
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|DynamicOptions
operator|.
name|DynamicBean
argument_list|>
name|dynamicBeans
decl_stmt|;
annotation|@
name|Inject
DECL|method|ParameterParser (CmdLineParser.Factory pf, DynamicMap<DynamicOptions.DynamicBean> dynamicBeans)
name|ParameterParser
parameter_list|(
name|CmdLineParser
operator|.
name|Factory
name|pf
parameter_list|,
name|DynamicMap
argument_list|<
name|DynamicOptions
operator|.
name|DynamicBean
argument_list|>
name|dynamicBeans
parameter_list|)
block|{
name|this
operator|.
name|parserFactory
operator|=
name|pf
expr_stmt|;
name|this
operator|.
name|dynamicBeans
operator|=
name|dynamicBeans
expr_stmt|;
block|}
DECL|method|parse ( T param, ListMultimap<String, String> in, HttpServletRequest req, HttpServletResponse res)
parameter_list|<
name|T
parameter_list|>
name|boolean
name|parse
parameter_list|(
name|T
name|param
parameter_list|,
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|in
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|CmdLineParser
name|clp
init|=
name|parserFactory
operator|.
name|create
argument_list|(
name|param
argument_list|)
decl_stmt|;
name|DynamicOptions
name|pluginOptions
init|=
operator|new
name|DynamicOptions
argument_list|(
name|param
argument_list|,
name|dynamicBeans
argument_list|)
decl_stmt|;
name|pluginOptions
operator|.
name|parseDynamicBeans
argument_list|(
name|clp
argument_list|)
expr_stmt|;
name|pluginOptions
operator|.
name|setDynamicBeans
argument_list|()
expr_stmt|;
name|pluginOptions
operator|.
name|onBeanParseStart
argument_list|()
expr_stmt|;
try|try
block|{
name|clp
operator|.
name|parseOptionMap
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CmdLineException
decl||
name|NumberFormatException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|clp
operator|.
name|wasHelpRequestedByOption
argument_list|()
condition|)
block|{
name|replyError
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|SC_BAD_REQUEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|clp
operator|.
name|wasHelpRequestedByOption
argument_list|()
condition|)
block|{
name|StringWriter
name|msg
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|clp
operator|.
name|printQueryStringUsage
argument_list|(
name|req
operator|.
name|getRequestURI
argument_list|()
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|clp
operator|.
name|printUsage
argument_list|(
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|replyBinaryResult
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|BinaryResult
operator|.
name|create
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|pluginOptions
operator|.
name|onBeanParseEnd
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|splitQueryString ( String queryString, ListMultimap<String, String> config, ListMultimap<String, String> params)
specifier|static
name|void
name|splitQueryString
parameter_list|(
name|String
name|queryString
parameter_list|,
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|config
parameter_list|,
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|queryString
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|kvPair
range|:
name|Splitter
operator|.
name|on
argument_list|(
literal|'&'
argument_list|)
operator|.
name|split
argument_list|(
name|queryString
argument_list|)
control|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|'='
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|split
argument_list|(
name|kvPair
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|Url
operator|.
name|decode
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|i
operator|.
name|hasNext
argument_list|()
condition|?
name|Url
operator|.
name|decode
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
else|:
literal|""
decl_stmt|;
if|if
condition|(
name|RESERVED_KEYS
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|config
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|query (HttpServletRequest req)
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|params
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|kvPair
range|:
name|Splitter
operator|.
name|on
argument_list|(
literal|'&'
argument_list|)
operator|.
name|split
argument_list|(
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|)
control|)
block|{
name|params
operator|.
name|add
argument_list|(
name|Iterables
operator|.
name|getFirst
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
literal|'='
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|split
argument_list|(
name|kvPair
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|params
return|;
block|}
comment|/**    * Convert a standard URL encoded form input into a parsed JSON tree.    *    *<p>Given an input such as:    *    *<pre>    * message=Does+not+compile.&labels.Verified=-1    *</pre>    *    * which is easily created using the curl command line tool:    *    *<pre>    * curl --data 'message=Does not compile.' --data labels.Verified=-1    *</pre>    *    * converts to a JSON object structure that is normally expected:    *    *<pre>    * {    *   "message": "Does not compile.",    *   "labels": {    *     "Verified": "-1"    *   }    * }    *</pre>    *    * This input can then be further processed into the Java input type expected by a view using    * Gson. Here we rely on Gson to perform implicit conversion of a string {@code "-1"} to a number    * type when the Java input type expects a number.    *    *<p>Conversion assumes any field name that does not contain {@code "."} will be a property of    * the top level input object. Any field with a dot will use the first segment as the top level    * property name naming an object, and the rest of the field name as a property in the nested    * object.    *    * @param req request to parse form input from and create JSON tree.    * @return the converted JSON object tree.    * @throws BadRequestException the request cannot be cast, as there are conflicting definitions    *     for a nested object.    */
DECL|method|formToJson (HttpServletRequest req)
specifier|static
name|JsonObject
name|formToJson
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
throws|throws
name|BadRequestException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
init|=
name|req
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
return|return
name|formToJson
argument_list|(
name|map
argument_list|,
name|query
argument_list|(
name|req
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|formToJson (Map<String, String[]> map, Set<String> query)
specifier|static
name|JsonObject
name|formToJson
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|query
parameter_list|)
throws|throws
name|BadRequestException
block|{
name|JsonObject
name|inputObject
init|=
operator|new
name|JsonObject
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
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
index|[]
name|values
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|contains
argument_list|(
name|key
argument_list|)
operator|||
name|values
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// Disallow processing query parameters as input body fields.
comment|// Implementations of views should avoid duplicate naming.
continue|continue;
block|}
name|JsonObject
name|obj
init|=
name|inputObject
decl_stmt|;
name|int
name|dot
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<=
name|dot
condition|)
block|{
name|String
name|property
init|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dot
argument_list|)
decl_stmt|;
name|JsonElement
name|e
init|=
name|inputObject
operator|.
name|get
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|obj
operator|=
operator|new
name|JsonObject
argument_list|()
expr_stmt|;
name|inputObject
operator|.
name|add
argument_list|(
name|property
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|isJsonObject
argument_list|()
condition|)
block|{
name|obj
operator|=
name|e
operator|.
name|getAsJsonObject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"key %s conflicts with %s"
argument_list|,
name|key
argument_list|,
name|property
argument_list|)
argument_list|)
throw|;
block|}
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
name|dot
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|obj
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// This error should never happen. If all form values are handled
comment|// together in a single pass properties are set only once. Setting
comment|// again indicates something has gone very wrong.
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"invalid form input, use JSON instead"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|obj
operator|.
name|addProperty
argument_list|(
name|key
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JsonArray
name|list
init|=
operator|new
name|JsonArray
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|values
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|JsonPrimitive
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|obj
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|inputObject
return|;
block|}
block|}
end_class

end_unit

