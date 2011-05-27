package main.users
{
	import main.users.tests.JoinServiceTest;
	import main.users.tests.UserServiceTest;

	[Suite]
	[RunWith("org.flexunit.runners.Suite")]
	public class UsersTestSuite
	{
		public var userServiceTest:UserServiceTest;
		public var joinServiceTest:JoinServiceTest;
	}
}