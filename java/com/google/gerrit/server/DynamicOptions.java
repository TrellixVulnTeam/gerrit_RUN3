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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
package|;
end_package

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
name|server
operator|.
name|plugins
operator|.
name|DelegatingClassLoader
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
name|inject
operator|.
name|Injector
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
name|Module
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
name|Provider
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import

begin_comment
comment|/** Helper class to define and parse options from plugins on ssh and RestAPI commands. */
end_comment

begin_class
DECL|class|DynamicOptions
specifier|public
class|class
name|DynamicOptions
block|{
comment|/**    * To provide additional options, bind a DynamicBean. For example:    *    *<pre>    *   bind(com.google.gerrit.server.DynamicOptions.DynamicBean.class)    *       .annotatedWith(Exports.named(com.google.gerrit.sshd.commands.Query.class))    *       .to(MyOptions.class);    *</pre>    *    * To define the additional options, implement this interface. For example:    *    *<pre>    *   public class MyOptions implements DynamicOptions.DynamicBean {    *     {@literal @}Option(name = "--verbose", aliases = {"-v"}    *             usage = "Make the operation more talkative")    *     public boolean verbose;    *   }    *</pre>    *    *<p>The option will be prefixed by the plugin name. In the example above, if the plugin name was    * my-plugin, then the --verbose option as used by the caller would be --my-plugin--verbose.    *    *<p>Additional options can be annotated with @RequiresOption which will cause them to be ignored    * unless the required option is present. For example:    *    *<pre>    *   {@literal @}RequiresOptions("--help")    *   {@literal @}Option(name = "--help-as-json",    *           usage = "display help text in json format")    *   public boolean displayHelpAsJson;    *</pre>    */
DECL|interface|DynamicBean
specifier|public
interface|interface
name|DynamicBean
block|{}
comment|/**    * To provide additional options to a command in another classloader, bind a ClassNameProvider    * which provides the name of your DynamicBean in the other classLoader.    *    *<p>Do this by binding to just the name of the command you are going to bind to so that your    * classLoader does not load the command's class which likely is not in your classpath. To ensure    * that the command's class is not in your classpath, you can exclude it during your build.    *    *<p>For example:    *    *<pre>    *   bind(com.google.gerrit.server.DynamicOptions.DynamicBean.class)    *       .annotatedWith(Exports.named( "com.google.gerrit.plugins.otherplugin.command"))    *       .to(MyOptionsClassNameProvider.class);    *    *   static class MyOptionsClassNameProvider implements DynamicOptions.ClassNameProvider {    *     @Override    *     public String getClassName() {    *       return "com.googlesource.gerrit.plugins.myplugin.CommandOptions";    *     }    *   }    *</pre>    */
DECL|interface|ClassNameProvider
specifier|public
interface|interface
name|ClassNameProvider
extends|extends
name|DynamicBean
block|{
DECL|method|getClassName ()
name|String
name|getClassName
parameter_list|()
function_decl|;
block|}
comment|/**    * To provide additional Guice bindings for options to a command in another classloader, bind a    * ModulesClassNamesProvider which provides the name of your Modules needed for your DynamicBean    * in the other classLoader.    *    *<p>Do this by binding to the name of the command you are going to bind to and providing an    * Iterable of Module names to instantiate and add to the Injector used to instantiate the    * DynamicBean in the other classLoader. For example:    *    *<pre>    *   bind(com.google.gerrit.server.DynamicOptions.DynamicBean.class)    *       .annotatedWith(Exports.named(    *           "com.google.gerrit.plugins.otherplugin.command"))    *       .to(MyOptionsModulesClassNamesProvider.class);    *    *   static class MyOptionsModulesClassNamesProvider implements DynamicOptions.ClassNameProvider {    *     @Override    *     public String getClassName() {    *       return "com.googlesource.gerrit.plugins.myplugin.CommandOptions";    *     }    *     @Override    *     public Iterable<String> getModulesClassNames()() {    *       return "com.googlesource.gerrit.plugins.myplugin.MyOptionsModule";    *     }    *   }    *</pre>    */
DECL|interface|ModulesClassNamesProvider
specifier|public
interface|interface
name|ModulesClassNamesProvider
extends|extends
name|ClassNameProvider
block|{
DECL|method|getModulesClassNames ()
name|Iterable
argument_list|<
name|String
argument_list|>
name|getModulesClassNames
parameter_list|()
function_decl|;
block|}
comment|/**    * Implement this if your DynamicBean needs an opportunity to act on the Bean directly before or    * after argument parsing.    */
DECL|interface|BeanParseListener
specifier|public
interface|interface
name|BeanParseListener
extends|extends
name|DynamicBean
block|{
DECL|method|onBeanParseStart (String plugin, Object bean)
name|void
name|onBeanParseStart
parameter_list|(
name|String
name|plugin
parameter_list|,
name|Object
name|bean
parameter_list|)
function_decl|;
DECL|method|onBeanParseEnd (String plugin, Object bean)
name|void
name|onBeanParseEnd
parameter_list|(
name|String
name|plugin
parameter_list|,
name|Object
name|bean
parameter_list|)
function_decl|;
block|}
comment|/**    * The entity which provided additional options may need a way to receive a reference to the    * DynamicBean it provided. To do so, the existing class should implement BeanReceiver (a setter)    * and then provide some way for the plugin to request its DynamicBean (a getter.) For example:    *    *<pre>    *   public class Query extends SshCommand implements DynamicOptions.BeanReceiver {    *       public void setDynamicBean(String plugin, DynamicOptions.DynamicBean dynamicBean) {    *         dynamicBeans.put(plugin, dynamicBean);    *       }    *    *       public DynamicOptions.DynamicBean getDynamicBean(String plugin) {    *         return dynamicBeans.get(plugin);    *       }    *   ...    *   }    * }    *</pre>    */
DECL|interface|BeanReceiver
specifier|public
interface|interface
name|BeanReceiver
block|{
DECL|method|setDynamicBean (String plugin, DynamicBean dynamicBean)
name|void
name|setDynamicBean
parameter_list|(
name|String
name|plugin
parameter_list|,
name|DynamicBean
name|dynamicBean
parameter_list|)
function_decl|;
block|}
comment|/**    * MergedClassloaders allow us to load classes from both plugin classloaders. Store the merged    * classloaders in a Map to avoid creating a new classloader for each invocation. Use a    * WeakHashMap to avoid leaking these MergedClassLoaders once either plugin is unloaded. Since the    * WeakHashMap only takes care of ensuring the Keys can get garbage collected, use WeakReferences    * to store the MergedClassloaders in the WeakHashMap.    *    *<p>Outter keys are the bean plugin's classloaders (the plugin being extended)    *    *<p>Inner keys are the dynamicBeans plugin's classloaders (the extending plugin)    *    *<p>The value is the MergedClassLoader representing the merging of the outter and inner key    * classloaders.    */
DECL|field|mergedClByCls
specifier|protected
specifier|static
name|Map
argument_list|<
name|ClassLoader
argument_list|,
name|Map
argument_list|<
name|ClassLoader
argument_list|,
name|WeakReference
argument_list|<
name|ClassLoader
argument_list|>
argument_list|>
argument_list|>
name|mergedClByCls
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|ClassLoader
argument_list|,
name|Map
argument_list|<
name|ClassLoader
argument_list|,
name|WeakReference
argument_list|<
name|ClassLoader
argument_list|>
argument_list|>
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|bean
specifier|protected
name|Object
name|bean
decl_stmt|;
DECL|field|beansByPlugin
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|DynamicBean
argument_list|>
name|beansByPlugin
decl_stmt|;
DECL|field|injector
specifier|protected
name|Injector
name|injector
decl_stmt|;
comment|/**    * Internal: For Gerrit to include options from DynamicBeans, setup a DynamicMap and instantiate    * this class so the following methods can be called if desired:    *    *<pre>    *    DynamicOptions pluginOptions = new DynamicOptions(bean, injector, dynamicBeans);    *    pluginOptions.parseDynamicBeans(clp);    *    pluginOptions.setDynamicBeans();    *    pluginOptions.onBeanParseStart();    *    *    // parse arguments here:  clp.parseArgument(argv);    *    *    pluginOptions.onBeanParseEnd();    *</pre>    */
DECL|method|DynamicOptions (Object bean, Injector injector, DynamicMap<DynamicBean> dynamicBeans)
specifier|public
name|DynamicOptions
parameter_list|(
name|Object
name|bean
parameter_list|,
name|Injector
name|injector
parameter_list|,
name|DynamicMap
argument_list|<
name|DynamicBean
argument_list|>
name|dynamicBeans
parameter_list|)
block|{
name|this
operator|.
name|bean
operator|=
name|bean
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|beansByPlugin
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|plugin
range|:
name|dynamicBeans
operator|.
name|plugins
argument_list|()
control|)
block|{
name|Provider
argument_list|<
name|DynamicBean
argument_list|>
name|provider
init|=
name|dynamicBeans
operator|.
name|byPlugin
argument_list|(
name|plugin
argument_list|)
operator|.
name|get
argument_list|(
name|bean
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|provider
operator|!=
literal|null
condition|)
block|{
name|beansByPlugin
operator|.
name|put
argument_list|(
name|plugin
argument_list|,
name|getDynamicBean
argument_list|(
name|bean
argument_list|,
name|provider
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getDynamicBean (Object bean, DynamicBean dynamicBean)
specifier|public
name|DynamicBean
name|getDynamicBean
parameter_list|(
name|Object
name|bean
parameter_list|,
name|DynamicBean
name|dynamicBean
parameter_list|)
block|{
name|ClassLoader
name|coreCl
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|ClassLoader
name|beanCl
init|=
name|bean
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|ClassLoader
name|loader
init|=
name|beanCl
decl_stmt|;
if|if
condition|(
name|beanCl
operator|!=
name|coreCl
condition|)
block|{
comment|// bean from a plugin?
name|ClassLoader
name|dynamicBeanCl
init|=
name|dynamicBean
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|beanCl
operator|!=
name|dynamicBeanCl
condition|)
block|{
comment|// in a different plugin?
name|loader
operator|=
name|getMergedClassLoader
argument_list|(
name|beanCl
argument_list|,
name|dynamicBeanCl
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|className
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dynamicBean
operator|instanceof
name|ClassNameProvider
condition|)
block|{
name|className
operator|=
operator|(
operator|(
name|ClassNameProvider
operator|)
name|dynamicBean
operator|)
operator|.
name|getClassName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|loader
operator|!=
name|beanCl
condition|)
block|{
comment|// in a different plugin?
name|className
operator|=
name|dynamicBean
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|className
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Injector
name|modulesInjector
init|=
name|injector
decl_stmt|;
if|if
condition|(
name|dynamicBean
operator|instanceof
name|ModulesClassNamesProvider
condition|)
block|{
name|modulesInjector
operator|=
name|injector
operator|.
name|createChildInjector
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|moduleName
range|:
operator|(
operator|(
name|ModulesClassNamesProvider
operator|)
name|dynamicBean
operator|)
operator|.
name|getModulesClassNames
argument_list|()
control|)
block|{
name|Class
argument_list|<
name|Module
argument_list|>
name|mClass
init|=
operator|(
name|Class
argument_list|<
name|Module
argument_list|>
operator|)
name|loader
operator|.
name|loadClass
argument_list|(
name|moduleName
argument_list|)
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|modulesInjector
operator|.
name|getInstance
argument_list|(
name|mClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|modulesInjector
operator|.
name|createChildInjector
argument_list|(
name|modules
argument_list|)
operator|.
name|getInstance
argument_list|(
operator|(
name|Class
argument_list|<
name|DynamicOptions
operator|.
name|DynamicBean
argument_list|>
operator|)
name|loader
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|dynamicBean
return|;
block|}
DECL|method|getMergedClassLoader (ClassLoader beanCl, ClassLoader dynamicBeanCl)
specifier|protected
name|ClassLoader
name|getMergedClassLoader
parameter_list|(
name|ClassLoader
name|beanCl
parameter_list|,
name|ClassLoader
name|dynamicBeanCl
parameter_list|)
block|{
name|Map
argument_list|<
name|ClassLoader
argument_list|,
name|WeakReference
argument_list|<
name|ClassLoader
argument_list|>
argument_list|>
name|mergedClByCl
init|=
name|mergedClByCls
operator|.
name|get
argument_list|(
name|beanCl
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergedClByCl
operator|==
literal|null
condition|)
block|{
name|mergedClByCl
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|mergedClByCls
operator|.
name|put
argument_list|(
name|beanCl
argument_list|,
name|mergedClByCl
argument_list|)
expr_stmt|;
block|}
name|WeakReference
argument_list|<
name|ClassLoader
argument_list|>
name|mergedClRef
init|=
name|mergedClByCl
operator|.
name|get
argument_list|(
name|dynamicBeanCl
argument_list|)
decl_stmt|;
name|ClassLoader
name|mergedCl
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|mergedClRef
operator|!=
literal|null
condition|)
block|{
name|mergedCl
operator|=
name|mergedClRef
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mergedCl
operator|==
literal|null
condition|)
block|{
name|mergedCl
operator|=
operator|new
name|DelegatingClassLoader
argument_list|(
name|beanCl
argument_list|,
name|dynamicBeanCl
argument_list|)
expr_stmt|;
name|mergedClByCl
operator|.
name|put
argument_list|(
name|dynamicBeanCl
argument_list|,
operator|new
name|WeakReference
argument_list|<>
argument_list|(
name|mergedCl
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|mergedCl
return|;
block|}
DECL|method|parseDynamicBeans (CmdLineParser clp)
specifier|public
name|void
name|parseDynamicBeans
parameter_list|(
name|CmdLineParser
name|clp
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|DynamicBean
argument_list|>
name|e
range|:
name|beansByPlugin
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|clp
operator|.
name|parseWithPrefix
argument_list|(
literal|"--"
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|clp
operator|.
name|drainOptionQueue
argument_list|()
expr_stmt|;
block|}
DECL|method|setDynamicBeans ()
specifier|public
name|void
name|setDynamicBeans
parameter_list|()
block|{
if|if
condition|(
name|bean
operator|instanceof
name|BeanReceiver
condition|)
block|{
name|BeanReceiver
name|receiver
init|=
operator|(
name|BeanReceiver
operator|)
name|bean
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|DynamicBean
argument_list|>
name|e
range|:
name|beansByPlugin
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|receiver
operator|.
name|setDynamicBean
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onBeanParseStart ()
specifier|public
name|void
name|onBeanParseStart
parameter_list|()
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|DynamicBean
argument_list|>
name|e
range|:
name|beansByPlugin
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DynamicBean
name|instance
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|instance
operator|instanceof
name|BeanParseListener
condition|)
block|{
name|BeanParseListener
name|listener
init|=
operator|(
name|BeanParseListener
operator|)
name|instance
decl_stmt|;
name|listener
operator|.
name|onBeanParseStart
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|bean
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onBeanParseEnd ()
specifier|public
name|void
name|onBeanParseEnd
parameter_list|()
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|DynamicBean
argument_list|>
name|e
range|:
name|beansByPlugin
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DynamicBean
name|instance
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|instance
operator|instanceof
name|BeanParseListener
condition|)
block|{
name|BeanParseListener
name|listener
init|=
operator|(
name|BeanParseListener
operator|)
name|instance
decl_stmt|;
name|listener
operator|.
name|onBeanParseEnd
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|bean
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

