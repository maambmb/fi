package util;

// type defs and utility functions for anonymous tuples
public class Tuple {

    // 2-tuple
    public static class Binary<T1,T2> {

        public T1 arg1;
        public T2 arg2;

        private Binary( T1 arg1, T2 arg2) {
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        @Override
        public int hashCode() {
            HashCoder.HASH_CODER.reset();
            HashCoder.HASH_CODER.addHash( this.arg1 );
            HashCoder.HASH_CODER.addHash( this.arg2 );
            return HashCoder.HASH_CODER.hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (getClass() != obj.getClass())
                return false;
            Tuple.Binary<?,?> other = (Tuple.Binary<?,?>) obj;
            return this.arg1.equals( other.arg1 ) && this.arg2.equals( other.arg2 );
        }

    }

    // 3-tuple
    public static class Ternary<T1,T2,T3> {

        public T1 arg1;
        public T2 arg2;
        public T3 arg3;

        private Ternary( T1 arg1, T2 arg2, T3 arg3) {
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
        }

        @Override
        public int hashCode() {
            HashCoder.HASH_CODER.reset();
            HashCoder.HASH_CODER.addHash( this.arg1 );
            HashCoder.HASH_CODER.addHash( this.arg2 );
            HashCoder.HASH_CODER.addHash( this.arg3 );
            return HashCoder.HASH_CODER.hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (getClass() != obj.getClass())
                return false;
            Tuple.Ternary<?,?,?> other = (Tuple.Ternary<?,?,?>) obj;
            return this.arg1.equals( other.arg1 ) && this.arg2.equals( other.arg2 ) && this.arg3.equals( other.arg3 );
        }

    }

    // use type inference to make these declarations slightly less verbose
    public static <T1,T2> Binary<T1,T2> create( T1 arg1, T2 arg2 ) {
        return new Binary<T1, T2>( arg1, arg2 );
    }

    // use type inference to make these declarations slightly less verbose
    public static <T1,T2,T3> Ternary<T1,T2,T3> create( T1 arg1, T2 arg2, T3 arg3 ) {
        return new Ternary<T1, T2, T3>( arg1, arg2, arg3 );
    }

}
