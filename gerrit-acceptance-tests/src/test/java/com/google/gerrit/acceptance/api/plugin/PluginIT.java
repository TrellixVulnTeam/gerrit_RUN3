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
DECL|package|com.google.gerrit.acceptance.api.plugin
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
name|plugin
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|GerritConfig
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
name|NoHttpd
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
name|RawInputUtil
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
name|plugins
operator|.
name|PluginApi
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
name|plugins
operator|.
name|Plugins
operator|.
name|ListRequest
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
name|InstallPluginInput
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
name|PluginInfo
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
name|RawInput
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
name|ResourceNotFoundException
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
name|java
operator|.
name|util
operator|.
name|List
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

begin_class
annotation|@
name|NoHttpd
DECL|class|PluginIT
specifier|public
class|class
name|PluginIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|JS_PLUGIN
specifier|private
specifier|static
specifier|final
name|String
name|JS_PLUGIN
init|=
literal|"Gerrit.install(function(self){});\n"
decl_stmt|;
DECL|field|HTML_PLUGIN
specifier|private
specifier|static
specifier|final
name|String
name|HTML_PLUGIN
init|=
name|String
operator|.
name|format
argument_list|(
literal|"<dom-module id=\"test\"><script>%s</script></dom-module>"
argument_list|,
name|JS_PLUGIN
argument_list|)
decl_stmt|;
DECL|field|JS_PLUGIN_CONTENT
specifier|private
specifier|static
specifier|final
name|RawInput
name|JS_PLUGIN_CONTENT
init|=
name|RawInputUtil
operator|.
name|create
argument_list|(
name|JS_PLUGIN
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|HTML_PLUGIN_CONTENT
specifier|private
specifier|static
specifier|final
name|RawInput
name|HTML_PLUGIN_CONTENT
init|=
name|RawInputUtil
operator|.
name|create
argument_list|(
name|HTML_PLUGIN
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|PLUGINS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|PLUGINS
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"plugin-a.js"
argument_list|,
literal|"plugin-b.html"
argument_list|,
literal|"plugin-c.js"
argument_list|,
literal|"plugin-d.html"
argument_list|,
literal|"plugin_e.js"
argument_list|)
decl_stmt|;
annotation|@
name|Test
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"plugins.allowRemoteAdmin"
argument_list|,
name|value
operator|=
literal|"true"
argument_list|)
DECL|method|pluginManagement ()
specifier|public
name|void
name|pluginManagement
parameter_list|()
throws|throws
name|Exception
block|{
comment|// No plugins are loaded
name|assertThat
argument_list|(
name|list
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|list
argument_list|()
operator|.
name|all
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|PluginApi
name|api
decl_stmt|;
comment|// Install all the plugins
name|InstallPluginInput
name|input
init|=
operator|new
name|InstallPluginInput
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|plugin
range|:
name|PLUGINS
control|)
block|{
name|input
operator|.
name|raw
operator|=
name|plugin
operator|.
name|endsWith
argument_list|(
literal|".js"
argument_list|)
condition|?
name|JS_PLUGIN_CONTENT
else|:
name|HTML_PLUGIN_CONTENT
expr_stmt|;
name|api
operator|=
name|gApi
operator|.
name|plugins
argument_list|()
operator|.
name|install
argument_list|(
name|plugin
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|api
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|PluginInfo
name|info
init|=
name|api
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|pluginName
argument_list|(
name|plugin
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|version
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|pluginVersion
argument_list|(
name|plugin
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|indexUrl
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"plugins/%s/"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|disabled
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
argument_list|)
expr_stmt|;
comment|// With pagination
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|start
argument_list|(
literal|1
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// With prefix
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|prefix
argument_list|(
literal|"plugin-b"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"plugin-b.html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|prefix
argument_list|(
literal|"PLUGIN-"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
comment|// With substring
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|substring
argument_list|(
literal|"lugin-"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|PLUGINS
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|substring
argument_list|(
literal|"lugin-"
argument_list|)
operator|.
name|start
argument_list|(
literal|1
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// With regex
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|regex
argument_list|(
literal|".*in-b"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"plugin-b.html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|regex
argument_list|(
literal|"plugin-.*"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|PLUGINS
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|regex
argument_list|(
literal|"plugin-.*"
argument_list|)
operator|.
name|start
argument_list|(
literal|1
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Invalid match combinations
name|assertBadRequest
argument_list|(
name|list
argument_list|()
operator|.
name|regex
argument_list|(
literal|".*in-b"
argument_list|)
operator|.
name|substring
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBadRequest
argument_list|(
name|list
argument_list|()
operator|.
name|regex
argument_list|(
literal|".*in-b"
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBadRequest
argument_list|(
name|list
argument_list|()
operator|.
name|substring
argument_list|(
literal|".*in-b"
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Disable
name|api
operator|=
name|gApi
operator|.
name|plugins
argument_list|()
operator|.
name|name
argument_list|(
literal|"plugin-a"
argument_list|)
expr_stmt|;
name|api
operator|.
name|disable
argument_list|()
expr_stmt|;
name|api
operator|=
name|gApi
operator|.
name|plugins
argument_list|()
operator|.
name|name
argument_list|(
literal|"plugin-a"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|api
operator|.
name|get
argument_list|()
operator|.
name|disabled
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|PLUGINS
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|all
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
argument_list|)
expr_stmt|;
comment|// Enable
name|api
operator|.
name|enable
argument_list|()
expr_stmt|;
name|api
operator|=
name|gApi
operator|.
name|plugins
argument_list|()
operator|.
name|name
argument_list|(
literal|"plugin-a"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|api
operator|.
name|get
argument_list|()
operator|.
name|disabled
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertPlugins
argument_list|(
name|list
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|PLUGINS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|installNotAllowed ()
specifier|public
name|void
name|installNotAllowed
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"remote installation is disabled"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|plugins
argument_list|()
operator|.
name|install
argument_list|(
literal|"test.js"
argument_list|,
operator|new
name|InstallPluginInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getNonExistingThrowsNotFound ()
specifier|public
name|void
name|getNonExistingThrowsNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|exception
operator|.
name|expect
argument_list|(
name|ResourceNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|plugins
argument_list|()
operator|.
name|name
argument_list|(
literal|"does-not-exist"
argument_list|)
expr_stmt|;
block|}
DECL|method|list ()
specifier|private
name|ListRequest
name|list
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|gApi
operator|.
name|plugins
argument_list|()
operator|.
name|list
argument_list|()
return|;
block|}
DECL|method|assertPlugins (List<PluginInfo> actual, List<String> expected)
specifier|private
name|void
name|assertPlugins
parameter_list|(
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|actual
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|_actual
init|=
name|actual
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|p
lambda|->
name|p
operator|.
name|id
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|_expected
init|=
name|expected
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|p
lambda|->
name|pluginName
argument_list|(
name|p
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|_actual
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|_expected
argument_list|)
expr_stmt|;
block|}
DECL|method|pluginName (String plugin)
specifier|private
name|String
name|pluginName
parameter_list|(
name|String
name|plugin
parameter_list|)
block|{
name|int
name|dot
init|=
name|plugin
operator|.
name|indexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|dot
argument_list|)
operator|.
name|isGreaterThan
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|plugin
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dot
argument_list|)
return|;
block|}
DECL|method|pluginVersion (String plugin)
specifier|private
name|String
name|pluginVersion
parameter_list|(
name|String
name|plugin
parameter_list|)
block|{
name|String
name|name
init|=
name|pluginName
argument_list|(
name|plugin
argument_list|)
decl_stmt|;
name|int
name|dash
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
return|return
name|dash
operator|>
literal|0
condition|?
name|name
operator|.
name|substring
argument_list|(
name|dash
operator|+
literal|1
argument_list|)
else|:
literal|""
return|;
block|}
DECL|method|assertBadRequest (ListRequest req)
specifier|private
name|void
name|assertBadRequest
parameter_list|(
name|ListRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|req
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected BadRequestException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadRequestException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
block|}
end_class

end_unit

