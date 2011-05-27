package 
{
	import main.modules.ModulesTestSuite;
	import main.users.UsersTestSuite;

	[Suite]
	[RunWith("org.flexunit.runners.Suite")]
	public class BBBTestSuite
	{
		public var modulesSuite:ModulesTestSuite;
		public var usersSuite:UsersTestSuite;
	}
}