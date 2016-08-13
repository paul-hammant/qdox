module M.N {
  requires A.B;
  requires public C.D;
  requires static E.F;
  requires public static G.H;

  exports P.Q;
  exports R.S to T1.U1, T2.U2;
  exports dynamic PP.QQ;
  exports dynamic RR.SS to T1.U1, T2.U2;

  uses V.W;
  provides X.Y with Z1.Z2;
  provides X.Y with Z3.Z4;
}