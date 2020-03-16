package com.mallivora.fabric.service.cauthds;

import org.hyperledger.fabric.protos.common.MspPrincipal;
public class Context {

    private int IDNum;

    private MspPrincipal.MSPPrincipal[] principals;

    public int getIDNum() {
        return IDNum;
    }

    public void setIDNum(int IDNum) {
        this.IDNum = IDNum;
    }

    public MspPrincipal.MSPPrincipal[] getPrincipals() {
        return principals;
    }

    public void setPrincipals(MspPrincipal.MSPPrincipal[] principals) {
        this.principals = principals;
    }

    public static Context newContext(){
        return new Context(){
            {
                setIDNum(0);
                setPrincipals(new MspPrincipal.MSPPrincipal[]{});
            }
        };
    }
}
