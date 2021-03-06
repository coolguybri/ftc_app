package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.MagneticFlux;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Temperature;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;


/**
 * This just runs from the position closest to the crater, into the crater.
 */
public abstract class DepotDescendSampleRun extends StandardChassis {

    private boolean madeTheRun = false;
    private GoldStatus pos = GoldStatus.Unknown;
    private long delay;


    public DepotDescendSampleRun(ChassisConfig config, long delay) {
        super(config);
        this.delay = delay;
    }

    /**
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        initMotors();
        initTimeouts();
        initSampling();
        initGyroscope();

    }


    /**
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop () {
        resetFlag();
    }

    /**
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start () {
        // Reset the game timer.
        runtime.reset();
    }

    /**
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop () {
        stopSampling();
    }

    float startingAngle = 0.0f;
    float endingAngle = 0.0f;

    /**
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop () {


        if (madeTheRun == false) {

            startingAngle = getGyroscopeAngle();

            pos = loopSampling();

            descendFromLander();

             if (pos == GoldStatus.Unknown)
                pos = sampleProbe();

             sleep(delay);

            //When gold is detected on the side of the screen it is on, strafe left, right or stay depending on where it is. Then, move forward into the crater.\

            depotSampleRun(pos);

            madeTheRun = true;
            endingAngle = getGyroscopeAngle();
        }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Sampling", "Pos: " + pos.toString());
        telemetry.addData("Status", "madeTheRun=%b, elapsed=%s", madeTheRun, runtime.toString());
        //telemetry.addData("Rotation", "start=%.0f, end=%.0f", startingAngle, endingAngle);
        telemetry.addData("Rotation", "start=" + startingAngle + ", end=" + endingAngle);
    }
}

