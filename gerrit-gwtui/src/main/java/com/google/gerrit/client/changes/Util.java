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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|core
operator|.
name|client
operator|.
name|GWT
import|;
end_import

begin_class
DECL|class|Util
specifier|public
class|class
name|Util
block|{
DECL|field|C
specifier|public
specifier|static
specifier|final
name|ChangeConstants
name|C
init|=
name|GWT
operator|.
name|create
argument_list|(
name|ChangeConstants
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|M
specifier|public
specifier|static
specifier|final
name|ChangeMessages
name|M
init|=
name|GWT
operator|.
name|create
argument_list|(
name|ChangeMessages
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SUBJECT_MAX_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|SUBJECT_MAX_LENGTH
init|=
literal|80
decl_stmt|;
DECL|field|SUBJECT_CROP_APPENDIX
specifier|private
specifier|static
specifier|final
name|String
name|SUBJECT_CROP_APPENDIX
init|=
literal|"..."
decl_stmt|;
DECL|field|SUBJECT_CROP_RANGE
specifier|private
specifier|static
specifier|final
name|int
name|SUBJECT_CROP_RANGE
init|=
literal|10
decl_stmt|;
DECL|method|toLongString (Change.Status status)
specifier|public
specifier|static
name|String
name|toLongString
parameter_list|(
name|Change
operator|.
name|Status
name|status
parameter_list|)
block|{
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|NEW
case|:
return|return
name|C
operator|.
name|statusLongNew
argument_list|()
return|;
case|case
name|MERGED
case|:
return|return
name|C
operator|.
name|statusLongMerged
argument_list|()
return|;
case|case
name|ABANDONED
case|:
return|return
name|C
operator|.
name|statusLongAbandoned
argument_list|()
return|;
default|default:
return|return
name|status
operator|.
name|name
argument_list|()
return|;
block|}
block|}
comment|/**    * Crops the given change subject if needed so that it has at most {@link #SUBJECT_MAX_LENGTH}    * characters.    *    *<p>If the given subject is not longer than {@link #SUBJECT_MAX_LENGTH} characters it is    * returned unchanged.    *    *<p>If the length of the given subject exceeds {@link #SUBJECT_MAX_LENGTH} characters it is    * cropped. In this case {@link #SUBJECT_CROP_APPENDIX} is appended to the cropped subject, the    * cropped subject including the appendix has at most {@link #SUBJECT_MAX_LENGTH} characters.    *    *<p>If cropping is needed, the subject will be cropped after the last space character that is    * found within the last {@link #SUBJECT_CROP_RANGE} characters of the potentially visible    * characters. If no such space is found, the subject will be cropped so that the cropped subject    * including the appendix has exactly {@link #SUBJECT_MAX_LENGTH} characters.    *    * @return the subject, cropped if needed    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|cropSubject (String subject)
specifier|public
specifier|static
name|String
name|cropSubject
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
if|if
condition|(
name|subject
operator|.
name|length
argument_list|()
operator|>
name|SUBJECT_MAX_LENGTH
condition|)
block|{
specifier|final
name|int
name|maxLength
init|=
name|SUBJECT_MAX_LENGTH
operator|-
name|SUBJECT_CROP_APPENDIX
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|cropPosition
init|=
name|maxLength
init|;
name|cropPosition
operator|>
name|maxLength
operator|-
name|SUBJECT_CROP_RANGE
condition|;
name|cropPosition
operator|--
control|)
block|{
comment|// Character.isWhitespace(char) can't be used because this method is not supported by GWT,
comment|// see https://developers.google.com/web-toolkit/doc/1.6/RefJreEmulation#Package_java_lang
if|if
condition|(
name|Character
operator|.
name|isSpace
argument_list|(
name|subject
operator|.
name|charAt
argument_list|(
name|cropPosition
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|subject
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|cropPosition
argument_list|)
operator|+
name|SUBJECT_CROP_APPENDIX
return|;
block|}
block|}
return|return
name|subject
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|maxLength
argument_list|)
operator|+
name|SUBJECT_CROP_APPENDIX
return|;
block|}
return|return
name|subject
return|;
block|}
block|}
end_class

end_unit

