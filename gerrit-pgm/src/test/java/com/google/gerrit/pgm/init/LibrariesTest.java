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
DECL|package|com.google.gerrit.pgm.init
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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|createStrictMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|verify
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
name|pgm
operator|.
name|util
operator|.
name|ConsoleUI
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
name|config
operator|.
name|SitePaths
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|assertNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_class
DECL|class|LibrariesTest
specifier|public
class|class
name|LibrariesTest
block|{
annotation|@
name|Test
DECL|method|testCreate ()
specifier|public
name|void
name|testCreate
parameter_list|()
throws|throws
name|FileNotFoundException
block|{
specifier|final
name|SitePaths
name|site
init|=
operator|new
name|SitePaths
argument_list|(
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ConsoleUI
name|ui
init|=
name|createStrictMock
argument_list|(
name|ConsoleUI
operator|.
name|class
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|ui
argument_list|)
expr_stmt|;
name|Libraries
name|lib
init|=
operator|new
name|Libraries
argument_list|(
operator|new
name|Provider
argument_list|<
name|LibraryDownloader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LibraryDownloader
name|get
parameter_list|()
block|{
return|return
operator|new
name|LibraryDownloader
argument_list|(
name|ui
argument_list|,
name|site
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|lib
operator|.
name|bouncyCastleProvider
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|lib
operator|.
name|mysqlDriver
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|ui
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

