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
DECL|package|net.codemirror.lib
package|package
name|net
operator|.
name|codemirror
operator|.
name|lib
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JavaScriptObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|dom
operator|.
name|client
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|dom
operator|.
name|client
operator|.
name|NativeEvent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
import|;
end_import

begin_comment
comment|/**  * Glue to connect CodeMirror to be callable from GWT.  *  * @link http://codemirror.net/doc/manual.html#api  */
end_comment

begin_class
DECL|class|CodeMirror
specifier|public
class|class
name|CodeMirror
extends|extends
name|JavaScriptObject
block|{
DECL|method|initLibrary (AsyncCallback<Void> cb)
specifier|public
specifier|static
name|void
name|initLibrary
parameter_list|(
name|AsyncCallback
argument_list|<
name|Void
argument_list|>
name|cb
parameter_list|)
block|{
name|Loader
operator|.
name|initLibrary
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|create (Element parent, Configuration cfg)
specifier|public
specifier|static
specifier|native
name|CodeMirror
name|create
parameter_list|(
name|Element
name|parent
parameter_list|,
name|Configuration
name|cfg
parameter_list|)
comment|/*-{     return $wnd.CodeMirror(parent, cfg);   }-*/
function_decl|;
DECL|method|setOption (String option, boolean value)
specifier|public
specifier|final
specifier|native
name|void
name|setOption
parameter_list|(
name|String
name|option
parameter_list|,
name|boolean
name|value
parameter_list|)
comment|/*-{     this.setOption(option, value);   }-*/
function_decl|;
DECL|method|setOption (String option, double value)
specifier|public
specifier|final
specifier|native
name|void
name|setOption
parameter_list|(
name|String
name|option
parameter_list|,
name|double
name|value
parameter_list|)
comment|/*-{     this.setOption(option, value);   }-*/
function_decl|;
DECL|method|setOption (String option, JavaScriptObject val)
specifier|public
specifier|final
specifier|native
name|void
name|setOption
parameter_list|(
name|String
name|option
parameter_list|,
name|JavaScriptObject
name|val
parameter_list|)
comment|/*-{     this.setOption(option, val);   }-*/
function_decl|;
DECL|method|setOptionToInfinity (String option)
specifier|public
specifier|final
specifier|native
name|void
name|setOptionToInfinity
parameter_list|(
name|String
name|option
parameter_list|)
comment|/*-{     this.setOption(option, Infinity);   }-*/
function_decl|;
DECL|method|setValue (String v)
specifier|public
specifier|final
specifier|native
name|void
name|setValue
parameter_list|(
name|String
name|v
parameter_list|)
comment|/*-{ this.setValue(v); }-*/
function_decl|;
DECL|method|setWidth (int w)
specifier|public
specifier|final
specifier|native
name|void
name|setWidth
parameter_list|(
name|int
name|w
parameter_list|)
comment|/*-{ this.setSize(w, null); }-*/
function_decl|;
DECL|method|setWidth (String w)
specifier|public
specifier|final
specifier|native
name|void
name|setWidth
parameter_list|(
name|String
name|w
parameter_list|)
comment|/*-{ this.setSize(w, null); }-*/
function_decl|;
DECL|method|setHeight (int h)
specifier|public
specifier|final
specifier|native
name|void
name|setHeight
parameter_list|(
name|int
name|h
parameter_list|)
comment|/*-{ this.setSize(null, h); }-*/
function_decl|;
DECL|method|setHeight (String h)
specifier|public
specifier|final
specifier|native
name|void
name|setHeight
parameter_list|(
name|String
name|h
parameter_list|)
comment|/*-{ this.setSize(null, h); }-*/
function_decl|;
DECL|method|refresh ()
specifier|public
specifier|final
specifier|native
name|void
name|refresh
parameter_list|()
comment|/*-{ this.refresh(); }-*/
function_decl|;
DECL|method|getWrapperElement ()
specifier|public
specifier|final
specifier|native
name|Element
name|getWrapperElement
parameter_list|()
comment|/*-{ return this.getWrapperElement(); }-*/
function_decl|;
DECL|method|markText (LineCharacter from, LineCharacter to, Configuration options)
specifier|public
specifier|final
specifier|native
name|TextMarker
name|markText
parameter_list|(
name|LineCharacter
name|from
parameter_list|,
name|LineCharacter
name|to
parameter_list|,
name|Configuration
name|options
parameter_list|)
comment|/*-{     return this.markText(from, to, options);   }-*/
function_decl|;
DECL|enum|LineClassWhere
specifier|public
enum|enum
name|LineClassWhere
block|{
DECL|enumConstant|TEXT
DECL|enumConstant|BACKGROUND
DECL|enumConstant|WRAP
name|TEXT
block|,
name|BACKGROUND
block|,
name|WRAP
block|;   }
DECL|method|addLineClass (int line, LineClassWhere where, String className)
specifier|public
specifier|final
name|void
name|addLineClass
parameter_list|(
name|int
name|line
parameter_list|,
name|LineClassWhere
name|where
parameter_list|,
name|String
name|className
parameter_list|)
block|{
name|addLineClassNative
argument_list|(
name|line
argument_list|,
name|where
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|className
argument_list|)
expr_stmt|;
block|}
DECL|method|addLineClassNative (int line, String where, String lineClass)
specifier|private
specifier|final
specifier|native
name|void
name|addLineClassNative
parameter_list|(
name|int
name|line
parameter_list|,
name|String
name|where
parameter_list|,
name|String
name|lineClass
parameter_list|)
comment|/*-{     this.addLineClass(line, where, lineClass);   }-*/
function_decl|;
DECL|method|addLineClass (LineHandle line, LineClassWhere where, String className)
specifier|public
specifier|final
name|void
name|addLineClass
parameter_list|(
name|LineHandle
name|line
parameter_list|,
name|LineClassWhere
name|where
parameter_list|,
name|String
name|className
parameter_list|)
block|{
name|addLineClassNative
argument_list|(
name|line
argument_list|,
name|where
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|className
argument_list|)
expr_stmt|;
block|}
DECL|method|addLineClassNative (LineHandle line, String where, String lineClass)
specifier|private
specifier|final
specifier|native
name|void
name|addLineClassNative
parameter_list|(
name|LineHandle
name|line
parameter_list|,
name|String
name|where
parameter_list|,
name|String
name|lineClass
parameter_list|)
comment|/*-{     this.addLineClass(line, where, lineClass);   }-*/
function_decl|;
DECL|method|removeLineClass (int line, LineClassWhere where, String className)
specifier|public
specifier|final
name|void
name|removeLineClass
parameter_list|(
name|int
name|line
parameter_list|,
name|LineClassWhere
name|where
parameter_list|,
name|String
name|className
parameter_list|)
block|{
name|removeLineClassNative
argument_list|(
name|line
argument_list|,
name|where
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|className
argument_list|)
expr_stmt|;
block|}
DECL|method|removeLineClassNative (int line, String where, String lineClass)
specifier|private
specifier|final
specifier|native
name|void
name|removeLineClassNative
parameter_list|(
name|int
name|line
parameter_list|,
name|String
name|where
parameter_list|,
name|String
name|lineClass
parameter_list|)
comment|/*-{     this.removeLineClass(line, where, lineClass);   }-*/
function_decl|;
DECL|method|removeLineClass (LineHandle line, LineClassWhere where, String className)
specifier|public
specifier|final
name|void
name|removeLineClass
parameter_list|(
name|LineHandle
name|line
parameter_list|,
name|LineClassWhere
name|where
parameter_list|,
name|String
name|className
parameter_list|)
block|{
name|removeLineClassNative
argument_list|(
name|line
argument_list|,
name|where
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|className
argument_list|)
expr_stmt|;
block|}
DECL|method|removeLineClassNative (LineHandle line, String where, String lineClass)
specifier|private
specifier|final
specifier|native
name|void
name|removeLineClassNative
parameter_list|(
name|LineHandle
name|line
parameter_list|,
name|String
name|where
parameter_list|,
name|String
name|lineClass
parameter_list|)
comment|/*-{     this.removeLineClass(line, where, lineClass);   }-*/
function_decl|;
DECL|method|addWidget (LineCharacter pos, Element node, boolean scrollIntoView)
specifier|public
specifier|final
specifier|native
name|void
name|addWidget
parameter_list|(
name|LineCharacter
name|pos
parameter_list|,
name|Element
name|node
parameter_list|,
name|boolean
name|scrollIntoView
parameter_list|)
comment|/*-{     this.addWidget(pos, node, scrollIntoView);   }-*/
function_decl|;
DECL|method|addLineWidget (int line, Element node, Configuration options)
specifier|public
specifier|final
specifier|native
name|LineWidget
name|addLineWidget
parameter_list|(
name|int
name|line
parameter_list|,
name|Element
name|node
parameter_list|,
name|Configuration
name|options
parameter_list|)
comment|/*-{     return this.addLineWidget(line, node, options);   }-*/
function_decl|;
DECL|method|lineAtHeight (int height)
specifier|public
specifier|final
specifier|native
name|int
name|lineAtHeight
parameter_list|(
name|int
name|height
parameter_list|)
comment|/*-{     return this.lineAtHeight(height);   }-*/
function_decl|;
DECL|method|getDoc ()
specifier|public
specifier|final
specifier|native
name|CodeMirrorDoc
name|getDoc
parameter_list|()
comment|/*-{     return this.getDoc();   }-*/
function_decl|;
DECL|method|scrollTo (int x, int y)
specifier|public
specifier|final
specifier|native
name|void
name|scrollTo
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|)
comment|/*-{     this.scrollTo(x, y);   }-*/
function_decl|;
DECL|method|scrollToY (int y)
specifier|public
specifier|final
specifier|native
name|void
name|scrollToY
parameter_list|(
name|int
name|y
parameter_list|)
comment|/*-{     this.scrollTo(null, y);   }-*/
function_decl|;
DECL|method|getScrollInfo ()
specifier|public
specifier|final
specifier|native
name|ScrollInfo
name|getScrollInfo
parameter_list|()
comment|/*-{     return this.getScrollInfo();   }-*/
function_decl|;
DECL|method|getScrollSetBy ()
specifier|public
specifier|final
specifier|native
name|CodeMirror
name|getScrollSetBy
parameter_list|()
comment|/*-{     return this.state.scrollSetBy;   }-*/
function_decl|;
DECL|method|setScrollSetBy (CodeMirror cm)
specifier|public
specifier|final
specifier|native
name|void
name|setScrollSetBy
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
comment|/*-{     this.state.scrollSetBy = cm;   }-*/
function_decl|;
DECL|method|getScrollSetAt ()
specifier|public
specifier|final
specifier|native
name|double
name|getScrollSetAt
parameter_list|()
comment|/*-{     return this.state.scrollSetAt;   }-*/
function_decl|;
DECL|method|setScrollSetAt (double when)
specifier|public
specifier|final
specifier|native
name|void
name|setScrollSetAt
parameter_list|(
name|double
name|when
parameter_list|)
comment|/*-{     this.state.scrollSetAt = when;   }-*/
function_decl|;
DECL|method|on (String event, Runnable thunk)
specifier|public
specifier|final
specifier|native
name|void
name|on
parameter_list|(
name|String
name|event
parameter_list|,
name|Runnable
name|thunk
parameter_list|)
comment|/*-{     this.on(event, $entry(function() {       thunk.@java.lang.Runnable::run()();     }));   }-*/
function_decl|;
comment|/** TODO: Break this line after updating GWT */
DECL|method|on (String event, EventHandler handler)
specifier|public
specifier|final
specifier|native
name|void
name|on
parameter_list|(
name|String
name|event
parameter_list|,
name|EventHandler
name|handler
parameter_list|)
comment|/*-{     this.on(event, $entry(function(cm, e) {       handler.@net.codemirror.lib.CodeMirror.EventHandler::handle(Lnet/codemirror/lib/CodeMirror;Lcom/google/gwt/dom/client/NativeEvent;)(cm, e);     }));   }-*/
function_decl|;
DECL|method|getCursor ()
specifier|public
specifier|final
specifier|native
name|LineCharacter
name|getCursor
parameter_list|()
comment|/*-{     return this.getCursor();   }-*/
function_decl|;
DECL|method|getCursor (String start)
specifier|public
specifier|final
specifier|native
name|LineCharacter
name|getCursor
parameter_list|(
name|String
name|start
parameter_list|)
comment|/*-{     return this.getCursor(start);   }-*/
function_decl|;
DECL|method|setCursor (LineCharacter lineCh)
specifier|public
specifier|final
specifier|native
name|void
name|setCursor
parameter_list|(
name|LineCharacter
name|lineCh
parameter_list|)
comment|/*-{     this.setCursor(lineCh);   }-*/
function_decl|;
DECL|method|somethingSelected ()
specifier|public
specifier|final
specifier|native
name|boolean
name|somethingSelected
parameter_list|()
comment|/*-{     return this.somethingSelected();   }-*/
function_decl|;
DECL|method|hasActiveLine ()
specifier|public
specifier|final
specifier|native
name|boolean
name|hasActiveLine
parameter_list|()
comment|/*-{     return this.state.hasOwnProperty('activeLine');   }-*/
function_decl|;
DECL|method|getActiveLine ()
specifier|public
specifier|final
specifier|native
name|LineHandle
name|getActiveLine
parameter_list|()
comment|/*-{     return this.state.activeLine;   }-*/
function_decl|;
DECL|method|setActiveLine (LineHandle line)
specifier|public
specifier|final
specifier|native
name|void
name|setActiveLine
parameter_list|(
name|LineHandle
name|line
parameter_list|)
comment|/*-{     this.state.activeLine = line;   }-*/
function_decl|;
DECL|method|addKeyMap (KeyMap map)
specifier|public
specifier|final
specifier|native
name|void
name|addKeyMap
parameter_list|(
name|KeyMap
name|map
parameter_list|)
comment|/*-{ this.addKeyMap(map); }-*/
function_decl|;
DECL|method|removeKeyMap (KeyMap map)
specifier|public
specifier|final
specifier|native
name|void
name|removeKeyMap
parameter_list|(
name|KeyMap
name|map
parameter_list|)
comment|/*-{ this.removeKeyMap(map); }-*/
function_decl|;
DECL|method|removeKeyMap (String name)
specifier|public
specifier|final
specifier|native
name|void
name|removeKeyMap
parameter_list|(
name|String
name|name
parameter_list|)
comment|/*-{ this.removeKeyMap(name); }-*/
function_decl|;
DECL|method|pos (int line, int ch)
specifier|public
specifier|static
specifier|final
specifier|native
name|LineCharacter
name|pos
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|ch
parameter_list|)
comment|/*-{     return $wnd.CodeMirror.Pos(line, ch);   }-*/
function_decl|;
DECL|method|pos (int line)
specifier|public
specifier|static
specifier|final
specifier|native
name|LineCharacter
name|pos
parameter_list|(
name|int
name|line
parameter_list|)
comment|/*-{     return $wnd.CodeMirror.Pos(line);   }-*/
function_decl|;
DECL|method|getLineHandle (int line)
specifier|public
specifier|final
specifier|native
name|LineHandle
name|getLineHandle
parameter_list|(
name|int
name|line
parameter_list|)
comment|/*-{     return this.getLineHandle(line);   }-*/
function_decl|;
DECL|method|getLineHandleVisualStart (int line)
specifier|public
specifier|final
specifier|native
name|LineHandle
name|getLineHandleVisualStart
parameter_list|(
name|int
name|line
parameter_list|)
comment|/*-{     return this.getLineHandleVisualStart(line);   }-*/
function_decl|;
DECL|method|getLineNumber (LineHandle handle)
specifier|public
specifier|final
specifier|native
name|int
name|getLineNumber
parameter_list|(
name|LineHandle
name|handle
parameter_list|)
comment|/*-{     return this.getLineNumber(handle);   }-*/
function_decl|;
DECL|method|focus ()
specifier|public
specifier|final
specifier|native
name|void
name|focus
parameter_list|()
comment|/*-{     this.focus();   }-*/
function_decl|;
comment|/** Hack into CodeMirror to disable unwanted keys */
DECL|method|disableUnwantedKey (String category, String name)
specifier|public
specifier|static
specifier|final
specifier|native
name|void
name|disableUnwantedKey
parameter_list|(
name|String
name|category
parameter_list|,
name|String
name|name
parameter_list|)
comment|/*-{     $wnd.CodeMirror.keyMap[category][name] = undefined;   }-*/
function_decl|;
DECL|method|defineVimEx (String name, String prefix, Runnable thunk)
specifier|public
specifier|static
specifier|native
name|void
name|defineVimEx
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|prefix
parameter_list|,
name|Runnable
name|thunk
parameter_list|)
comment|/*-{     $wnd.CodeMirror.Vim.defineEx(name, prefix, $entry(function() {       thunk.@java.lang.Runnable::run()();     }));   }-*/
function_decl|;
DECL|method|getGutterElement ()
specifier|public
specifier|final
specifier|native
name|Element
name|getGutterElement
parameter_list|()
comment|/*-{     return this.getGutterElement();   }-*/
function_decl|;
DECL|method|CodeMirror ()
specifier|protected
name|CodeMirror
parameter_list|()
block|{   }
DECL|class|LineHandle
specifier|public
specifier|static
class|class
name|LineHandle
extends|extends
name|JavaScriptObject
block|{
DECL|method|LineHandle ()
specifier|protected
name|LineHandle
parameter_list|()
block|{     }
block|}
DECL|interface|EventHandler
specifier|public
interface|interface
name|EventHandler
block|{
DECL|method|handle (CodeMirror instance, NativeEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|CodeMirror
name|instance
parameter_list|,
name|NativeEvent
name|event
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

