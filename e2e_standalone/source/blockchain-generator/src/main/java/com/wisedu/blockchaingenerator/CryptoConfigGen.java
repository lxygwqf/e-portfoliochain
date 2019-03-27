package com.wisedu.blockchaingenerator;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.*;

public class CryptoConfigGen {
    public void gen() throws IOException {
        try
        {
            Velocity.init();
        }
        catch(Exception e)
        {
            System.out.println("Problem initializing Velocity : " + e );
            return;
        }

        /* lets make a Context and put data into it */

        VelocityContext context = new VelocityContext();

        /* lets render a generator */

        StringWriter w = new StringWriter();

        try
        {
            Velocity.mergeTemplate("vm/crypto-config.vm", "ISO-8859-1", context, w );
        }
        catch (Exception e )
        {
            System.out.println("Problem merging generator : " + e );
        }
        System.out.println(w);
        File file=new File("crypto-config.yaml");
        File yaml = file;
        BufferedWriter writer = new BufferedWriter(new FileWriter(yaml));
        writer.write(w.toString());
        writer.flush();
        writer.close();
    }
} 