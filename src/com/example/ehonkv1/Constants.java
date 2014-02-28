package com.example.ehonkv1;

import java.util.ArrayList;

public class Constants {

	public static String PROFILE_FILE_NAME = "EHonk4";

	public static ArrayList<String> REGION_LIST = new ArrayList<String>();

	public static void populate() {
		// inserez judete
		REGION_LIST.add("ab");
		REGION_LIST.add("ag");
		REGION_LIST.add("ar");
		REGION_LIST.add("bc");
		REGION_LIST.add("bh");
		REGION_LIST.add("bn");
		REGION_LIST.add("br");
		REGION_LIST.add("bt");
		REGION_LIST.add("bv");
		REGION_LIST.add("bz");
		REGION_LIST.add("cj");
		REGION_LIST.add("cl");
		REGION_LIST.add("cs");
		REGION_LIST.add("ct");
		REGION_LIST.add("cv");
		REGION_LIST.add("db");
		REGION_LIST.add("dj");
		REGION_LIST.add("gj");
		REGION_LIST.add("gl");
		REGION_LIST.add("gr");
		REGION_LIST.add("hd");
		REGION_LIST.add("hr");
		REGION_LIST.add("if");
		REGION_LIST.add("il");
		REGION_LIST.add("is");
		REGION_LIST.add("mh");
		REGION_LIST.add("mm");
		REGION_LIST.add("ms");
		REGION_LIST.add("nt");
		REGION_LIST.add("ot");
		REGION_LIST.add("ph");
		REGION_LIST.add("sb");
		REGION_LIST.add("sj");
		REGION_LIST.add("sm");
		REGION_LIST.add("sv");
		REGION_LIST.add("tl");
		REGION_LIST.add("tm");
		REGION_LIST.add("tr");
		REGION_LIST.add("vl");
		REGION_LIST.add("vn");
		REGION_LIST.add("vs");
	}

	public static boolean checkCarNo(String carNo) {
		boolean ok = true;

		carNo.toLowerCase();
		if (carNo.length() < 5)
			return false;
		else if (carNo.charAt(0) == 'b' && carNo.charAt(1) != 'c'
				&& carNo.charAt(1) != 'h' && carNo.charAt(1) != 'n'
				&& carNo.charAt(1) != 'r' && carNo.charAt(1) != 't'
				&& carNo.charAt(1) != 'v' && carNo.charAt(1) != 'z') {
			if (carNo.charAt(1) < '0' || carNo.charAt(1) > '9'
					|| carNo.charAt(2) < '0' || carNo.charAt(2) > '9')
				return false;
			if (carNo.length() == 6) {
				// urmatoarele trebuie sa fie litere
				if (carNo.charAt(3) < 'a' || carNo.charAt(3) > 'z'
						|| carNo.charAt(4) < 'a'
						|| carNo.charAt(4) > 'z'
						|| carNo.charAt(5) < 'a'
						|| carNo.charAt(5) > 'z')

					return false;

			} else if (carNo.length() == 7) {
				if (carNo.charAt(3) < '0' || carNo.charAt(3) > '9')
					return false;
				// urmatoarele trebuie sa fie litere
				else if (carNo.charAt(6) < 'a' || carNo.charAt(6) > 'z'
						|| carNo.charAt(4) < 'a'
						|| carNo.charAt(4) > 'z'
						|| carNo.charAt(5) < 'a'
						|| carNo.charAt(5) > 'z')
					return false;

			} else
				return false;
		} else {
			// pentru celelalte judete
			int ind = Constants.REGION_LIST.indexOf(carNo.substring(0, 2));
			if (ind == -1)
				return false;
			else {
				if (carNo.charAt(2) < '0' || carNo.charAt(2) > '9'
						|| carNo.charAt(3) < '0'
						|| carNo.charAt(3) > '9')
					return false;
				if (carNo.length() == 7) {
					// urmatoarele trebuie sa fie litere
					if (carNo.charAt(4) < 'a' || carNo.charAt(4) > 'z'
							|| carNo.charAt(5) < 'a'
							|| carNo.charAt(5) > 'z'
							|| carNo.charAt(6) < 'a'
							|| carNo.charAt(6) > 'z')

						return false;

				} else if (carNo.length() == 8) {
					if (carNo.charAt(4) < '0' || carNo.charAt(4) > '9')
						return false;
					// urmatoarele trebuie sa fie litere
					else if (carNo.charAt(5) < 'a'
							|| carNo.charAt(5) > 'z'
							|| carNo.charAt(6) < 'a'
							|| carNo.charAt(6) > 'z'
							|| carNo.charAt(7) < 'a'
							|| carNo.charAt(7) > 'z')
						return false;

				} else
					return false;
			}
		}
		return ok;
	}
}
