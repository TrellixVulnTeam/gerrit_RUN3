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
DECL|package|com.google.gerrit.pgm.init.api
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
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
name|common
operator|.
name|Die
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Console
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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

begin_comment
comment|/** Console based interaction with the invoking user. */
end_comment

begin_class
DECL|class|ConsoleUI
specifier|public
specifier|abstract
class|class
name|ConsoleUI
block|{
comment|/** Get a UI instance, assuming interactive mode. */
DECL|method|getInstance ()
specifier|public
specifier|static
name|ConsoleUI
name|getInstance
parameter_list|()
block|{
return|return
name|getInstance
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/** Get a UI instance, possibly forcing batch mode. */
DECL|method|getInstance (final boolean batchMode)
specifier|public
specifier|static
name|ConsoleUI
name|getInstance
parameter_list|(
specifier|final
name|boolean
name|batchMode
parameter_list|)
block|{
name|Console
name|console
init|=
name|batchMode
condition|?
literal|null
else|:
name|System
operator|.
name|console
argument_list|()
decl_stmt|;
return|return
name|console
operator|!=
literal|null
condition|?
operator|new
name|Interactive
argument_list|(
name|console
argument_list|)
else|:
operator|new
name|Batch
argument_list|()
return|;
block|}
comment|/** Constructs an exception indicating the user aborted the operation. */
DECL|method|abort ()
specifier|protected
specifier|static
name|Die
name|abort
parameter_list|()
block|{
return|return
operator|new
name|Die
argument_list|(
literal|"aborted by user"
argument_list|)
return|;
block|}
comment|/** @return true if this is a batch UI that has no user interaction. */
DECL|method|isBatch ()
specifier|public
specifier|abstract
name|boolean
name|isBatch
parameter_list|()
function_decl|;
comment|/** Display a header message before a series of prompts. */
DECL|method|header (String fmt, Object... args)
specifier|public
specifier|abstract
name|void
name|header
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
comment|/** Display a message. */
DECL|method|message (String fmt, Object... args)
specifier|public
specifier|abstract
name|void
name|message
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
comment|/** Request the user to answer a yes/no question. */
DECL|method|yesno (Boolean def, String fmt, Object... args)
specifier|public
specifier|abstract
name|boolean
name|yesno
parameter_list|(
name|Boolean
name|def
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
comment|/** Prints a message asking the user to let us know when its safe to continue. */
DECL|method|waitForUser ()
specifier|public
specifier|abstract
name|void
name|waitForUser
parameter_list|()
function_decl|;
comment|/** Prompt the user for a string, suggesting a default, and returning choice. */
DECL|method|readString (String def, String fmt, Object... args)
specifier|public
specifier|abstract
name|String
name|readString
parameter_list|(
name|String
name|def
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
comment|/** Prompt the user to make a choice from an allowed list of values. */
DECL|method|readString ( String def, Set<String> allowedValues, String fmt, Object... args)
specifier|public
specifier|abstract
name|String
name|readString
parameter_list|(
name|String
name|def
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
comment|/** Prompt the user for an integer value, suggesting a default. */
DECL|method|readInt (int def, String fmt, Object... args)
specifier|public
name|int
name|readInt
parameter_list|(
name|int
name|def
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|String
name|p
init|=
name|readString
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|def
argument_list|)
argument_list|,
name|fmt
argument_list|,
name|args
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|p
operator|.
name|trim
argument_list|()
argument_list|,
literal|10
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: Invalid integer format: "
operator|+
name|p
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Prompt the user for a password, returning the string; null if blank. */
DECL|method|password (String fmt, Object... args)
specifier|public
specifier|abstract
name|String
name|password
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
comment|/** Display an error message on the system stderr. */
DECL|method|error (String format, Object... args)
specifier|public
name|void
name|error
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/** Prompt the user to make a choice from an enumeration's values. */
DECL|method|readEnum ( T def, A options, String fmt, Object... args)
specifier|public
specifier|abstract
parameter_list|<
name|T
extends|extends
name|Enum
argument_list|<
name|?
argument_list|>
parameter_list|,
name|A
extends|extends
name|EnumSet
argument_list|<
name|?
extends|extends
name|T
argument_list|>
parameter_list|>
name|T
name|readEnum
parameter_list|(
name|T
name|def
parameter_list|,
name|A
name|options
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
DECL|class|Interactive
specifier|private
specifier|static
class|class
name|Interactive
extends|extends
name|ConsoleUI
block|{
DECL|field|console
specifier|private
specifier|final
name|Console
name|console
decl_stmt|;
DECL|method|Interactive (final Console console)
name|Interactive
parameter_list|(
specifier|final
name|Console
name|console
parameter_list|)
block|{
name|this
operator|.
name|console
operator|=
name|console
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isBatch ()
specifier|public
name|boolean
name|isBatch
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|yesno (Boolean def, String fmt, Object... args)
specifier|public
name|boolean
name|yesno
parameter_list|(
name|Boolean
name|def
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
specifier|final
name|String
name|prompt
init|=
name|String
operator|.
name|format
argument_list|(
name|fmt
argument_list|,
name|args
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|String
name|y
init|=
literal|"y"
decl_stmt|;
name|String
name|n
init|=
literal|"n"
decl_stmt|;
if|if
condition|(
name|def
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|def
condition|)
block|{
name|y
operator|=
literal|"Y"
expr_stmt|;
block|}
else|else
block|{
name|n
operator|=
literal|"N"
expr_stmt|;
block|}
block|}
name|String
name|yn
init|=
name|console
operator|.
name|readLine
argument_list|(
literal|"%-30s [%s/%s]? "
argument_list|,
name|prompt
argument_list|,
name|y
argument_list|,
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|yn
operator|==
literal|null
condition|)
block|{
throw|throw
name|abort
argument_list|()
throw|;
block|}
name|yn
operator|=
name|yn
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|def
operator|!=
literal|null
operator|&&
name|yn
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|def
return|;
block|}
if|if
condition|(
name|yn
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"y"
argument_list|)
operator|||
name|yn
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|yn
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"n"
argument_list|)
operator|||
name|yn
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|waitForUser ()
specifier|public
name|void
name|waitForUser
parameter_list|()
block|{
if|if
condition|(
name|console
operator|.
name|readLine
argument_list|(
literal|"Press enter to continue "
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
name|abort
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|readString (String def, String fmt, Object... args)
specifier|public
name|String
name|readString
parameter_list|(
name|String
name|def
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
specifier|final
name|String
name|prompt
init|=
name|String
operator|.
name|format
argument_list|(
name|fmt
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|String
name|r
decl_stmt|;
if|if
condition|(
name|def
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
name|console
operator|.
name|readLine
argument_list|(
literal|"%-30s [%s]: "
argument_list|,
name|prompt
argument_list|,
name|def
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|r
operator|=
name|console
operator|.
name|readLine
argument_list|(
literal|"%-30s : "
argument_list|,
name|prompt
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
throw|throw
name|abort
argument_list|()
throw|;
block|}
name|r
operator|=
name|r
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|def
return|;
block|}
return|return
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|readString (String def, Set<String> allowedValues, String fmt, Object... args)
specifier|public
name|String
name|readString
parameter_list|(
name|String
name|def
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|String
name|r
init|=
name|readString
argument_list|(
name|def
argument_list|,
name|fmt
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowedValues
operator|.
name|contains
argument_list|(
name|r
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|r
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
literal|"?"
operator|.
name|equals
argument_list|(
name|r
argument_list|)
condition|)
block|{
name|console
operator|.
name|printf
argument_list|(
literal|"error: '%s' is not a valid choice\n"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
name|console
operator|.
name|printf
argument_list|(
literal|"       Supported options are:\n"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|v
range|:
name|allowedValues
control|)
block|{
name|console
operator|.
name|printf
argument_list|(
literal|"         %s\n"
argument_list|,
name|v
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|password (String fmt, Object... args)
specifier|public
name|String
name|password
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
specifier|final
name|String
name|prompt
init|=
name|String
operator|.
name|format
argument_list|(
name|fmt
argument_list|,
name|args
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|char
index|[]
name|a1
init|=
name|console
operator|.
name|readPassword
argument_list|(
literal|"%-30s : "
argument_list|,
name|prompt
argument_list|)
decl_stmt|;
if|if
condition|(
name|a1
operator|==
literal|null
condition|)
block|{
throw|throw
name|abort
argument_list|()
throw|;
block|}
specifier|final
name|char
index|[]
name|a2
init|=
name|console
operator|.
name|readPassword
argument_list|(
literal|"%30s : "
argument_list|,
literal|"confirm password"
argument_list|)
decl_stmt|;
if|if
condition|(
name|a2
operator|==
literal|null
condition|)
block|{
throw|throw
name|abort
argument_list|()
throw|;
block|}
specifier|final
name|String
name|s1
init|=
operator|new
name|String
argument_list|(
name|a1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s2
init|=
operator|new
name|String
argument_list|(
name|a2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
condition|)
block|{
name|console
operator|.
name|printf
argument_list|(
literal|"error: Passwords did not match; try again\n"
argument_list|)
expr_stmt|;
continue|continue;
block|}
return|return
operator|!
name|s1
operator|.
name|isEmpty
argument_list|()
condition|?
name|s1
else|:
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readEnum ( T def, A options, String fmt, Object... args)
specifier|public
parameter_list|<
name|T
extends|extends
name|Enum
argument_list|<
name|?
argument_list|>
parameter_list|,
name|A
extends|extends
name|EnumSet
argument_list|<
name|?
extends|extends
name|T
argument_list|>
parameter_list|>
name|T
name|readEnum
parameter_list|(
name|T
name|def
parameter_list|,
name|A
name|options
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
specifier|final
name|String
name|prompt
init|=
name|String
operator|.
name|format
argument_list|(
name|fmt
argument_list|,
name|args
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|String
name|r
init|=
name|console
operator|.
name|readLine
argument_list|(
literal|"%-30s [%s/?]: "
argument_list|,
name|prompt
argument_list|,
name|def
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
throw|throw
name|abort
argument_list|()
throw|;
block|}
name|r
operator|=
name|r
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|def
return|;
block|}
for|for
control|(
specifier|final
name|T
name|e
range|:
name|options
control|)
block|{
if|if
condition|(
name|e
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|r
argument_list|)
condition|)
block|{
return|return
name|e
return|;
block|}
block|}
if|if
condition|(
operator|!
literal|"?"
operator|.
name|equals
argument_list|(
name|r
argument_list|)
condition|)
block|{
name|console
operator|.
name|printf
argument_list|(
literal|"error: '%s' is not a valid choice\n"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
name|console
operator|.
name|printf
argument_list|(
literal|"       Supported options are:\n"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|T
name|e
range|:
name|options
control|)
block|{
name|console
operator|.
name|printf
argument_list|(
literal|"         %s\n"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|header (String fmt, Object... args)
specifier|public
name|void
name|header
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|fmt
operator|=
name|fmt
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|"\n*** "
argument_list|)
expr_stmt|;
name|console
operator|.
name|printf
argument_list|(
literal|"\n*** "
operator|+
name|fmt
operator|+
literal|"\n*** \n\n"
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|message (String fmt, Object... args)
specifier|public
name|void
name|message
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|console
operator|.
name|printf
argument_list|(
name|fmt
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Batch
specifier|private
specifier|static
class|class
name|Batch
extends|extends
name|ConsoleUI
block|{
annotation|@
name|Override
DECL|method|isBatch ()
specifier|public
name|boolean
name|isBatch
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|yesno (Boolean def, String fmt, Object... args)
specifier|public
name|boolean
name|yesno
parameter_list|(
name|Boolean
name|def
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|def
operator|!=
literal|null
condition|?
name|def
else|:
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|readString (String def, String fmt, Object... args)
specifier|public
name|String
name|readString
parameter_list|(
name|String
name|def
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|def
return|;
block|}
annotation|@
name|Override
DECL|method|readString (String def, Set<String> allowedValues, String fmt, Object... args)
specifier|public
name|String
name|readString
parameter_list|(
name|String
name|def
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|def
return|;
block|}
annotation|@
name|Override
DECL|method|waitForUser ()
specifier|public
name|void
name|waitForUser
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|password (String fmt, Object... args)
specifier|public
name|String
name|password
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|readEnum ( T def, A options, String fmt, Object... args)
specifier|public
parameter_list|<
name|T
extends|extends
name|Enum
argument_list|<
name|?
argument_list|>
parameter_list|,
name|A
extends|extends
name|EnumSet
argument_list|<
name|?
extends|extends
name|T
argument_list|>
parameter_list|>
name|T
name|readEnum
parameter_list|(
name|T
name|def
parameter_list|,
name|A
name|options
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|def
return|;
block|}
annotation|@
name|Override
DECL|method|header (String fmt, Object... args)
specifier|public
name|void
name|header
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|message (String fmt, Object... args)
specifier|public
name|void
name|message
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{}
block|}
block|}
end_class

end_unit

