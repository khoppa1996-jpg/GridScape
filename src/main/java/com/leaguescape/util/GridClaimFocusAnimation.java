package com.leaguescape.util;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Timer;
import java.util.function.Consumer;

/**
 * Smooth zoom + scroll focus after claiming a grid tile (default 100ms).
 */
public final class GridClaimFocusAnimation
{
	public static final int CLAIM_FOCUS_DURATION_MS = 100;
	private static final int STEPS = 10;

	private GridClaimFocusAnimation() {}

	/**
	 * View position to center the viewport on a cell in a uniform grid layout (matches task / world unlock grids).
	 *
	 * @param ringExtent half the grid width in cells minus center (same as maxRing / center index)
	 */
	public static Point computeViewPositionForTile(JViewport viewport, JPanel gridPanel, int row, int col,
		int ringExtent, int tileSize, int cellPaddingTotal)
	{
		int gx = col + ringExtent;
		int gy = ringExtent - row;
		int cellW = tileSize + cellPaddingTotal;
		int cellH = cellW;
		int px = gx * cellW;
		int py = gy * cellH;
		int vw = viewport.getExtentSize().width;
		int vh = viewport.getExtentSize().height;
		Dimension viewSize = gridPanel.getPreferredSize();
		int maxX = Math.max(0, viewSize.width - vw);
		int maxY = Math.max(0, viewSize.height - vh);
		int vpx = Math.max(0, Math.min(px - vw / 2 + cellW / 2, maxX));
		int vpy = Math.max(0, Math.min(py - vh / 2 + cellH / 2, maxY));
		return new Point(vpx, vpy);
	}

	/**
	 * Animates zoom from start to end over {@link #CLAIM_FOCUS_DURATION_MS} with smoothstep, calling
	 * {@code refresh} after each step. If zoom span is negligible, runs one refresh and completes.
	 */
	public static void animateZoomToClaim(float zoomStart, float zoomEnd, float zoomMin, float zoomMax,
		Consumer<Float> setZoom, Runnable refresh, Runnable onComplete)
	{
		float z0 = Math.max(zoomMin, Math.min(zoomMax, zoomStart));
		float z1 = Math.max(zoomMin, Math.min(zoomMax, zoomEnd));
		if (Math.abs(z1 - z0) < 0.0005f)
		{
			if (refresh != null)
			{
				refresh.run();
			}
			if (onComplete != null)
			{
				onComplete.run();
			}
			return;
		}
		final int[] step = { 0 };
		int delay = Math.max(1, CLAIM_FOCUS_DURATION_MS / STEPS);
		Timer timer = new Timer(delay, null);
		timer.addActionListener(e -> {
			step[0]++;
			float p = Math.min(1f, step[0] / (float) STEPS);
			float t = p * p * (3f - 2f * p);
			float z = z0 + (z1 - z0) * t;
			z = Math.max(zoomMin, Math.min(zoomMax, z));
			setZoom.accept(z);
			if (refresh != null)
			{
				refresh.run();
			}
			if (step[0] >= STEPS)
			{
				timer.stop();
				setZoom.accept(z1);
				if (refresh != null)
				{
					refresh.run();
				}
				if (onComplete != null)
				{
					onComplete.run();
				}
			}
		});
		timer.setRepeats(true);
		timer.start();
	}
}
