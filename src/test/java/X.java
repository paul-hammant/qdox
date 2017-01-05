public class X
{
    enum EnumWithConstructors
    {
        c( "hello" ), d();

        int someField;

        EnumWithConstructors()
        {
        }

        EnumWithConstructors( String x )
        {
        }
    }
}
