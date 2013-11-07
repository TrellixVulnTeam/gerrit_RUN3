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
name|gerrit
operator|.
name|server
operator|.
name|util
operator|.
name|SocketUtil
operator|.
name|hostname
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
name|util
operator|.
name|SocketUtil
operator|.
name|isIPv6
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
name|util
operator|.
name|SocketUtil
operator|.
name|parse
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
name|util
operator|.
name|SocketUtil
operator|.
name|resolve
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|InetAddress
operator|.
name|getByName
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
operator|.
name|createUnresolved
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
name|assertEquals
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
name|assertFalse
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
name|assertTrue
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Inet4Address
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Inet6Address
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_class
DECL|class|SocketUtilTest
specifier|public
class|class
name|SocketUtilTest
block|{
annotation|@
name|Test
DECL|method|testIsIPv6 ()
specifier|public
name|void
name|testIsIPv6
parameter_list|()
throws|throws
name|UnknownHostException
block|{
specifier|final
name|InetAddress
name|ipv6
init|=
name|getByName
argument_list|(
literal|"1:2:3:4:5:6:7:8"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ipv6
operator|instanceof
name|Inet6Address
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isIPv6
argument_list|(
name|ipv6
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|InetAddress
name|ipv4
init|=
name|getByName
argument_list|(
literal|"127.0.0.1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ipv4
operator|instanceof
name|Inet4Address
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isIPv6
argument_list|(
name|ipv4
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHostname ()
specifier|public
name|void
name|testHostname
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"*"
argument_list|,
name|hostname
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|80
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost"
argument_list|,
name|hostname
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|80
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|hostname
argument_list|(
name|createUnresolved
argument_list|(
literal|"foo"
argument_list|,
literal|80
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFormat ()
specifier|public
name|void
name|testFormat
parameter_list|()
throws|throws
name|UnknownHostException
block|{
name|assertEquals
argument_list|(
literal|"*:1234"
argument_list|,
name|SocketUtil
operator|.
name|format
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|1234
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"*"
argument_list|,
name|SocketUtil
operator|.
name|format
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|80
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo:1234"
argument_list|,
name|SocketUtil
operator|.
name|format
argument_list|(
name|createUnresolved
argument_list|(
literal|"foo"
argument_list|,
literal|1234
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|SocketUtil
operator|.
name|format
argument_list|(
name|createUnresolved
argument_list|(
literal|"foo"
argument_list|,
literal|80
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1:2:3:4:5:6:7:8]:1234"
argument_list|,
comment|//
name|SocketUtil
operator|.
name|format
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|getByName
argument_list|(
literal|"1:2:3:4:5:6:7:8"
argument_list|)
argument_list|,
literal|1234
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1:2:3:4:5:6:7:8]"
argument_list|,
comment|//
name|SocketUtil
operator|.
name|format
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|getByName
argument_list|(
literal|"1:2:3:4:5:6:7:8"
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:1234"
argument_list|,
comment|//
name|SocketUtil
operator|.
name|format
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost"
argument_list|,
comment|//
name|SocketUtil
operator|.
name|format
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|80
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParse ()
specifier|public
name|void
name|testParse
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|1234
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"*:1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|80
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"*"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|1234
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|":1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|80
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|""
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createUnresolved
argument_list|(
literal|"1:2:3:4:5:6:7:8"
argument_list|,
literal|1234
argument_list|)
argument_list|,
comment|//
name|parse
argument_list|(
literal|"[1:2:3:4:5:6:7:8]:1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createUnresolved
argument_list|(
literal|"1:2:3:4:5:6:7:8"
argument_list|,
literal|80
argument_list|)
argument_list|,
comment|//
name|parse
argument_list|(
literal|"[1:2:3:4:5:6:7:8]"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createUnresolved
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
argument_list|,
comment|//
name|parse
argument_list|(
literal|"[localhost]:1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createUnresolved
argument_list|(
literal|"localhost"
argument_list|,
literal|80
argument_list|)
argument_list|,
comment|//
name|parse
argument_list|(
literal|"[localhost]"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createUnresolved
argument_list|(
literal|"foo.bar.example.com"
argument_list|,
literal|1234
argument_list|)
argument_list|,
comment|//
name|parse
argument_list|(
literal|"[foo.bar.example.com]:1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createUnresolved
argument_list|(
literal|"foo.bar.example.com"
argument_list|,
literal|80
argument_list|)
argument_list|,
comment|//
name|parse
argument_list|(
literal|"[foo.bar.example.com]"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|parse
argument_list|(
literal|"[:3"
argument_list|,
literal|80
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not throw exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"invalid IPv6: [:3"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|parse
argument_list|(
literal|"localhost:A"
argument_list|,
literal|80
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not throw exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"invalid port: localhost:A"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testResolve ()
specifier|public
name|void
name|testResolve
parameter_list|()
throws|throws
name|UnknownHostException
block|{
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|1234
argument_list|)
argument_list|,
name|resolve
argument_list|(
literal|"*:1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|80
argument_list|)
argument_list|,
name|resolve
argument_list|(
literal|"*"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|1234
argument_list|)
argument_list|,
name|resolve
argument_list|(
literal|":1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|80
argument_list|)
argument_list|,
name|resolve
argument_list|(
literal|""
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|getByName
argument_list|(
literal|"1:2:3:4:5:6:7:8"
argument_list|)
argument_list|,
literal|1234
argument_list|)
argument_list|,
comment|//
name|resolve
argument_list|(
literal|"[1:2:3:4:5:6:7:8]:1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|getByName
argument_list|(
literal|"1:2:3:4:5:6:7:8"
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|,
comment|//
name|resolve
argument_list|(
literal|"[1:2:3:4:5:6:7:8]"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|getByName
argument_list|(
literal|"localhost"
argument_list|)
argument_list|,
literal|1234
argument_list|)
argument_list|,
comment|//
name|resolve
argument_list|(
literal|"[localhost]:1234"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|getByName
argument_list|(
literal|"localhost"
argument_list|)
argument_list|,
literal|80
argument_list|)
argument_list|,
comment|//
name|resolve
argument_list|(
literal|"[localhost]"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

