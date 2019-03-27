package com.wisedu.blockchaingenerator;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.*;

public class ConfigtxGen {
    public void gen() throws IOException {
        try {
            Velocity.init();
        } catch (Exception e) {
            System.out.println("Problem initializing Velocity : " + e);
            return;
        }


        VelocityContext context = new VelocityContext();


        StringWriter w = new StringWriter();

        try {
            Velocity.mergeTemplate("vm/configtx.vm", "ISO-8859-1", context, w);
        } catch (Exception e) {
            System.out.println("Problem merging generator : " + e);
        }

        File yaml = new File("configtx.yaml");
        BufferedWriter writer = new BufferedWriter(new FileWriter(yaml));
        writer.write(w.toString());
        writer.flush();
        writer.close();
    }
} 