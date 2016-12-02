@Bar
module M.N {
  requires A.B;
  requires transitive C.D;
  requires static E.F;
  requires transitive static G.H;

  exports P.Q;
  exports R.S to T1.U1, T2.U2;

  opens P.Q;
  opens R.S to T1.U1, T2.U2;

  uses V.W;
  provides X.Y with Z1.Z2, Z3.Z4;
}